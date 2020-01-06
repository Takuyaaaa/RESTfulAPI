package com.example.restfulapi.exception;

import com.example.restfulapi.controller.OauthController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpServerErrorException;

/**
 * ExceptionHandlerクラス
 *
 * @author Natsume Takuya
 */
@ControllerAdvice(assignableTypes = OauthController.class)
@Slf4j
public class OauthExceptionHandler {

  /**
   * 401 : Profile画面確認時にログインしてない場合に対応
   *
   * @param ex UnauthorizedExceptionクラス
   * @return ResponseEntity<Object> json形式のレスポンス
   */
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler({UnauthorizedException.class})
  public String handleUnauthorizedException(UnauthorizedException ex) {
    log.warn(ex.getMessage(), ex);
    return "error/401";
  }

  /**
   * 404 : URLが無効な場合などに対応
   *
   * @param ex NotFoundExceptionクラス
   * @return ResponseEntity<Object> json形式のレスポンス
   */
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler({NotFoundException.class})
  public String handleUNotFoundException(NotFoundException ex) {
    log.warn(ex.getMessage(), ex);
    return "error/404";
  }

  /**
   * 500 : 不明なサーバーエラーに対応
   *
   * @param ex UnauthorizedExceptionクラス
   * @return ResponseEntity<Object> json形式のレスポンス
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({HttpServerErrorException.InternalServerError.class})
  public String handleInternalServerError(HttpServerErrorException.InternalServerError ex) {
    log.error(ex.getMessage(), ex);
    return "error/500";
  }
}
