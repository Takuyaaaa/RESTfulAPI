package com.example.restfulapi.controller;

import com.example.restfulapi.dto.CalculatedLogInfoDto;
import com.example.restfulapi.form.DateForm;
import com.example.restfulapi.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Locale;

/**
 * Logの管理画面に対応するコントローラークラス
 *
 * @author Natsume Takuya
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class LogController {

  private final LogService logService;
  private final MessageSource messageSource;

  /**
   * ログ管理画面を表示する
   *
   * @param modelAndView ModelAndViewクラス
   * @return ModelAndView ログ管理画面に対応
   */
  @GetMapping("/api-log")
  public ModelAndView manageLog(
      @ModelAttribute("dataForm") DateForm dateForm, ModelAndView modelAndView) throws IOException {
    modelAndView.setViewName("log");
    modelAndView.addObject("dateValid", true);
    modelAndView.addObject("calculatedLogInfo", new CalculatedLogInfoDto());
    return modelAndView;
  }

  /**
   * ログ記録参照の処理を行う
   *
   * @param dateForm 入力された開始日、終了日を格納するformクラス
   * @param bindingResult BindingResultクラス
   * @param modelAndView ModelAndViewクラス
   * @return ModelAndView ログ集計結果画面に対応
   */
  @GetMapping("/api-log/search")
  public ModelAndView searchLog(
      @Validated(DateForm.All.class) @ModelAttribute("dataForm") DateForm dateForm,
      BindingResult bindingResult,
      ModelAndView modelAndView) {
    if (bindingResult.hasErrors()) {
      modelAndView.addObject("calculatedLogInfo", new CalculatedLogInfoDto());
      modelAndView.setViewName("log");
      return modelAndView;
    }

    if (logService.isLogContentDtoListEmpty(dateForm.getStartDate(), dateForm.getEndDate())) {

      modelAndView.addObject(
          "result", messageSource.getMessage("apilog.search.noresult", null, Locale.JAPAN));
    }
    modelAndView.setViewName("log");

    CalculatedLogInfoDto calculatedLogInfoDto =
            logService.passCalculateDatToDto(dateForm.getStartDate(), dateForm.getEndDate());
    modelAndView.addObject("calculatedLogInfo", calculatedLogInfoDto);
    return modelAndView;
  }
}
