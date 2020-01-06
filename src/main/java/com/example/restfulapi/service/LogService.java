package com.example.restfulapi.service;

import com.example.restfulapi.constant.UrlPatterns;
import com.example.restfulapi.dto.CalculatedLogInfoDto;
import com.example.restfulapi.dto.LogContentsDto;
import com.example.restfulapi.entity.LogContents;
import com.example.restfulapi.repository.LogContentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summarizingDouble;

/**
 * Serviceクラス
 *
 * @author Natsume Takuya
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class LogService {

  private static final LocalDate DATE_FOR_AGGREGATION = LocalDate.now().minus(Period.ofDays(1));
  private final LogContentsRepository logContentsRepository;

  /** 一連のログ読み込み処理を行う */
  public void processLogContent() throws IOException {
    log.info("対象のログファイル : ApiLog.{}.tsv", DATE_FOR_AGGREGATION);
    if (wasFileReadBefore()) {
      log.error("ApiLog.{}.tsvはすでに読み込まれています", DATE_FOR_AGGREGATION);
      log.info("ログの集計を終了します");
      return;
    }

    log.info("APIログの読込・集計を開始します");
    Map<ImmutableTriple<String, String, Integer>, DoubleSummaryStatistics> logMap = processLog();
    log.info("APIログの読込・集計が完了しました");
    log.info("APIログの保存を行います");
    saveAggregatedLog(logMap);
    log.info("保存が完了しました");
  }

  /**
   * ログを読み込み、apiのアクセス内容を取得
   *
   * @return List<LogContentsDto> ログDTOのリスト
   * @throws IOException IOExceptionクラス
   */
  private Map<ImmutableTriple<String, String, Integer>, DoubleSummaryStatistics> processLog()
      throws IOException {

    return Files.lines(
            Paths.get("./src/main/resources/Log/ApiLog." + DATE_FOR_AGGREGATION + ".tsv"),
            StandardCharsets.UTF_8)
        .map(apiLog -> acquireAdiInfo(Arrays.asList(apiLog.split("\t"))))
        .filter(logContentsDto -> !"Invalid Request".equals(logContentsDto.getApiName()))
        .collect(
            groupingBy(
                logContentsDto ->
                    ImmutableTriple.of(
                        logContentsDto.getApiName(),
                        logContentsDto.getHttpMethod(),
                        logContentsDto.getHttpStatusCode()),
                summarizingDouble(LogContentsDto::getExecutionTime)));
  }

  /**
   * ログリストからapi情報を取得
   *
   * @param logList ログファイルから取得したapiアクセス内容
   * @return LogContentsDto LogContentsDtoクラス
   */
  private LogContentsDto acquireAdiInfo(List<String> logList) {

    try {

      int executionTime = Integer.parseInt(logList.get(0));

      String httpMethod = logList.get(1);

      String apiName = logList.get(2);

      apiName = UrlPatterns.checkApiName(apiName, httpMethod);

      int httpStatusCode = Integer.parseInt(logList.get(3));

      String date = logList.get(4);

      DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);

      return convertToDto(executionTime, httpMethod, httpStatusCode, apiName, localDate);
    } catch (RuntimeException ex) {
      log.error("ログの書式が無効です", ex);
      return LogContentsDto.builder().apiName("Invalid Request").build();
    }
  }

  /**
   * 集計したログ情報を保存する
   *
   * @param logMap 集計されたログ情報
   */
  private void saveAggregatedLog(
      Map<ImmutableTriple<String, String, Integer>, DoubleSummaryStatistics> logMap) {

    logMap.keySet().stream()
        .map(log -> convertToLogContents(log, logMap))
        .forEach(logContentsRepository::save);
  }

  /**
   * ログ情報をLogContentsクラスに変換
   *
   * @param log ログ情報を表すImmutableTripleクラス
   * @param logMap ログ情報を表すMapクラス
   * @return logContents LogContentsクラス
   */
  private LogContents convertToLogContents(
      ImmutableTriple<String, String, Integer> log,
      Map<ImmutableTriple<String, String, Integer>, DoubleSummaryStatistics> logMap) {

    LogContents logContents = new LogContents();
    logContents.setApiName(log.getLeft());
    logContents.setHttpMethod(log.getMiddle());
    logContents.setHttpStatusCode(log.getRight());
    logContents.setExecutionTime((int) logMap.get(log).getAverage());
    logContents.setAccessCount((int) logMap.get(log).getCount());
    logContents.setDate(DATE_FOR_AGGREGATION);
    logContents.setFileName("ApiLog." + DATE_FOR_AGGREGATION + ".tsv");
    return logContents;
  }

  /**
   * Logの検索を行い、DTOクラスのリストとして返す
   *
   * @param startDate 検索の開始日
   * @param endDate 検索の終了日
   * @return List<LogContentsDto> 検索結果のリスト
   */
  private List<LogContentsDto> searchLogContentsAsDto(LocalDate startDate, LocalDate endDate) {
    List<String> logList = logContentsRepository.findByDateAndReCalculate(startDate, endDate);

    return logList.stream()
        .map(
            log ->
                    convertSummaryToDto(convertStringListToLogContents(Arrays.asList(log.split(",")))))
        .collect(Collectors.toList());
  }

  /**
   * StringのリストをDtoクラスのリストに変換
   *
   * @param log ログ情報を含むStringのリスト
   * @return logContents LogContents
   */
  private LogContents convertStringListToLogContents(List<String> log) {
    LogContents logContents = new LogContents();
    logContents.setApiName(log.get(0));
    logContents.setHttpMethod(log.get(1));
    logContents.setHttpStatusCode(Integer.parseInt(log.get(2)));
    logContents.setAccessCount(Integer.parseInt(log.get(3)));
    logContents.setExecutionTime(Double.parseDouble(log.get(4)));
    return logContents;
  }

  /**
   * StringをLocalDate型に変換
   *
   * @param date String型のdate
   * @return LocalDate LocalDate型のdate
   */
  private LocalDate convertStringToLocalDate(String date) {
    return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }

  /**
   * DBに保存する項目をもとにDTOクラスに変換
   *
   * @param executionTime 実行時間の平均
   * @param httpMethod httpMethod
   * @param httpStatusCode httpStatusCode
   * @param apiName APIの名称
   * @param localDate LocalDateクラス
   * @return LogContentsDto LogContentsDtoクラス
   */
  private LogContentsDto convertToDto(
      double executionTime,
      String httpMethod,
      int httpStatusCode,
      String apiName,
      LocalDate localDate) {
    return LogContentsDto.builder()
        .apiName(apiName)
        .httpMethod(httpMethod)
        .httpStatusCode(httpStatusCode)
        .executionTime(executionTime)
        .date(localDate)
        .build();
  }

  /**
   * エンティティクラスをDTOクラスに変換
   *
   * @param logContents LogContentsクラス
   * @return LogContentsDto LogContentsDtoクラス
   */
  private LogContentsDto convertSummaryToDto(LogContents logContents) {
    return LogContentsDto.builder()
        .apiName(logContents.getApiName())
        .httpMethod(logContents.getHttpMethod())
        .httpStatusCode(logContents.getHttpStatusCode())
        .accessCount(logContents.getAccessCount())
        .executionTime(logContents.getExecutionTime())
        .build();
  }

  /**
   * APIログ集計参照のための開始日、終了日とその結果のリストをDTOクラスに変換
   *
   * @param startDate 開始日
   * @param endDate 終了日
   * @return CalculatedLogInfoDto CalculatedLogInfoDtoクラス
   */
  public CalculatedLogInfoDto passCalculateDatToDto(String startDate, String endDate) {
    LocalDate startLocalDate = convertStringToLocalDate(startDate);
    LocalDate endLocalDate = convertStringToLocalDate(endDate);
    CalculatedLogInfoDto calculatedLogInfoDto = new CalculatedLogInfoDto();
    calculatedLogInfoDto.setStartLocalDate(startLocalDate);
    calculatedLogInfoDto.setEndLocalDate(endLocalDate);
    calculatedLogInfoDto.setLogContentsDtoList(
            searchLogContentsAsDto(startLocalDate, endLocalDate));
    return calculatedLogInfoDto;
  }

  /**
   * ログコンテンツの検索結果が存在するか判定
   *
   * @param startDate 開始日
   * @param endDate 終了日
   * @return boolean 存在しなければtrue
   */
  public boolean isLogContentDtoListEmpty(String startDate, String endDate) {
    LocalDate startLocalDate = convertStringToLocalDate(startDate);
    LocalDate endLocalDate = convertStringToLocalDate(endDate);
    List<LogContentsDto> logContentsDtoList = searchLogContentsAsDto(startLocalDate, endLocalDate);
    return (logContentsDtoList.size() == 0);
  }

  /**
   * 該当ファイルが以前に読み込まれたかを判別
   *
   * <p>boolean 読み込まれたことがある場合はtrue
   */
  private boolean wasFileReadBefore() {
    return !logContentsRepository
        .findByFileName("ApiLog." + DATE_FOR_AGGREGATION + ".tsv")
        .isEmpty();
  }
}
