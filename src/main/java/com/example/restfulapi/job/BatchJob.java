package com.example.restfulapi.job;

import com.example.restfulapi.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 定期ジョブの内容を定義するバッチジョブクラス
 *
 * @author Natsume Takuya
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class BatchJob {

  private final LogService logService;

  /** 毎日決められた時間に実行されるログ集計処理 */
  @Scheduled(cron = "${cron.batchCycle}", zone = "${cron.timeZone}")
  public void aggregateApiLog() {

    try {
      log.info("APIログの集計を開始します");
      logService.processLogContent();
      log.info("ログの集計を終了します");
    } catch (RuntimeException | IOException ex) {
      log.error("ログの読み込み中にエラーが発生しました", ex);
      log.info("ログの読み込みを終了します");
    }
  }
}
