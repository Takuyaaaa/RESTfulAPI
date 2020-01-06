package com.example.restfulapi.exception;

import com.example.restfulapi.controller.ProductController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

/**
 * ExceptionHandlerクラス
 *
 * @author Natsume Takuya
 */
@RestControllerAdvice(assignableTypes = ProductController.class)
@Slf4j
@RequiredArgsConstructor
public class ProductExceptionHandler extends ResponseEntityExceptionHandler {

  private final MessageSource messageSource;

  /**
   * 400 : Product登録時の名前重複、画像更新時の拡張子不正などに対応
   *
   * @param ex BadRequestExceptionクラス
   * @param request WebRequestクラス
   * @return ResponseEntity<Object> json形式のレスポンス
   */
  @ExceptionHandler({BadRequestException.class})
  public ResponseEntity<Object> handleBadRequestException(
      BadRequestException ex, WebRequest request) {
    log.warn(ex.getMessage(), ex);
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String errorLog = ex.getMessage();
    ErrorResponse body =
        new ErrorResponse(
                messageSource.getMessage("error.products.invalid.detail", null, Locale.JAPAN),
            errorLog);

    return handleExceptionInternal(ex, body, headers, status, request);
  }

  /**
   * 401 : Tokenが有効でない場合などに対応
   *
   * @param ex UnauthorizedExceptionクラス
   * @param request WebRequestクラス
   * @return ResponseEntity<Object> json形式のレスポンス
   */
  @ExceptionHandler({UnauthorizedException.class})
  public ResponseEntity<Object> handleUnauthorizedException(
      UnauthorizedException ex, WebRequest request) {
    log.warn(ex.getMessage(), ex);
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.UNAUTHORIZED;
    String errorLog = ex.getMessage();
    ErrorResponse body =
        new ErrorResponse(
                messageSource.getMessage("error.products.unauthorized", null, Locale.JAPAN), errorLog);

    return handleExceptionInternal(ex, body, headers, status, request);
  }

  /**
   * 415 : アップされた画像のメディアタイプ不正に対応
   *
   * @param ex UnsupportedMediaExceptionクラス
   * @param request WebRequestクラス
   * @return ResponseEntity<Object> json形式のレスポンス
   */
  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<Object> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    log.warn(ex.getMessage(), ex);
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
    String errorLog = ex.getMessage();
    ErrorResponse body =
        new ErrorResponse(
                messageSource.getMessage("error.products.image.suffix", null, Locale.JAPAN), errorLog);

    return handleExceptionInternal(ex, body, headers, status, request);
  }

  /**
   * 404 : IDでの検索や編集を行なった際に対象Productが存在しない場合に対応
   *
   * @param ex NotFoundExceptionクラス
   * @param request WebRequestクラス
   * @return ResponseEntity<Object> json形式のレスポンス
   */
  @ExceptionHandler({NotFoundException.class})
  public ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
    log.warn(ex.getMessage(), ex);
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.NOT_FOUND;
    String errorLog = ex.getMessage();
    ErrorResponse body =
        new ErrorResponse(
                messageSource.getMessage("error.products.notfound.detail", null, Locale.JAPAN),
            errorLog);

    return handleExceptionInternal(ex, body, headers, status, request);
  }

  /**
   * 404 : 画像の取得を行う際にその画像が存在しない場合に対応
   *
   * @param ex FileNotFoundExceptionクラス
   * @param request WebRequestクラス
   * @return ResponseEntity<Object> json形式のレスポンス
   */
  @ExceptionHandler({FileNotFoundException.class})
  public ResponseEntity<Object> handleFileNotFoundException(
      FileNotFoundException ex, WebRequest request) {
    log.warn(ex.getMessage(), ex);
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.NOT_FOUND;
    String errorLog = ex.getMessage();
    ErrorResponse body =
        new ErrorResponse(
                messageSource.getMessage("error.products.image.notfound.detail", null, Locale.JAPAN),
            errorLog);
    return handleExceptionInternal(ex, body, headers, status, request);
  }

  /**
   * 404 : URLが無効な場合
   *
   * @param ex MethodArgumentTypeMismatchExceptionクラス
   * @param request WebRequestクラス
   * @return ResponseEntity<Object> json形式のレスポンス
   */
  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    log.warn(ex.getMessage(), ex);
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.NOT_FOUND;
    String errorLog = ex.getMessage();
    ErrorResponse body =
        new ErrorResponse(
                messageSource.getMessage("error.products.notSupported.detail", null, Locale.JAPAN),
            errorLog);
    return handleExceptionInternal(ex, body, headers, status, request);
  }

  /**
   * 500 : ディレクトリの作成やファイルの処理のエラーに対応
   *
   * @param ex FileNotFoundExceptionクラス
   * @param request WebRequestクラス
   * @return ResponseEntity<Object> json形式のレスポンス
   */
  @ExceptionHandler({IOException.class})
  public ResponseEntity<Object> handleIOException(IOException ex, WebRequest request) {
    log.warn(ex.getMessage(), ex);
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String errorLog = ex.getMessage();
    ErrorResponse body =
        new ErrorResponse(
                messageSource.getMessage("error.products.ioe", null, Locale.JAPAN), errorLog);
    return handleExceptionInternal(ex, body, headers, status, request);
  }

  /**
   * 500 : 上記で扱われていないサーバーエラーに対応
   *
   * @param ex Exceptionクラス
   * @param request WebRequestクラス
   * @return ResponseEntity<Object> json形式のレスポンス
   */
  @ExceptionHandler({Exception.class})
  @ResponseBody
  public ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest request) {
    log.error(ex.getMessage(), ex);
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String errorLog = ex.getMessage();
    ErrorResponse body =
        new ErrorResponse(
                messageSource.getMessage("error.products.general", null, Locale.JAPAN), errorLog);

    return handleExceptionInternal(ex, body, headers, status, request);
  }

  /**
   * エラーをキャッチした際に、そのレスポンスjsonを整形するメソッド
   *
   * @param ex 当該のExceptionクラス
   * @param body Objectクラス
   * @param headers HttpHeadersクラス
   * @param status HttpStatusクラス
   * @param request WebRequestクラス
   * @return ResponseEntity<Object> json形式のレスポンス
   */
  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

    System.out.println("passing Error handling process");
    //    String errorLog = ex.getMessage();
    //    if (!(body instanceof ErrorResponse)) {
    //      body = new ErrorResponse(message, errorLog);
    //    }
    return new ResponseEntity<>(body, headers, status);
  }

  /**
   * 400 : formクラスのvalidationに対応
   *
   * @param ex MethodArgumentNotValidExceptionクラス
   * @param headers HttpHeadersクラス
   * @param status HttpStatusクラス
   * @param request WebRequestクラス
   * @return ResponseEntity<Object> json形式のレスポンス
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    log.warn(ex.getMessage(), ex);
    String errorLog = ex.getMessage();
    return handleExceptionInternal(
        ex,
        new ErrorResponse(
                messageSource.getMessage("error.products.invalid.detail", null, Locale.JAPAN),
            errorLog),
        headers,
        status,
        request);
  }

  /**
   * 400 : formクラスのprice範囲validationに対応
   *
   * @param ex HttpMessageNotReadableExceptionクラス
   * @param headers HttpHeadersクラス
   * @param status HttpStatusクラス
   * @param request WebRequestクラス
   * @return ResponseEntity<Object> json形式のレスポンス
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    log.warn(ex.getMessage(), ex);
    String errorLog = ex.getMessage();
    return handleExceptionInternal(
        ex,
        new ErrorResponse(
                messageSource.getMessage("error.products.invalid.detail", null, Locale.JAPAN),
            errorLog),
        headers,
        status,
        request);
  }

  /**
   * 405 : HTTPメソッドが無効の場合に対応
   *
   * @param ex HttpRequestMethodNotSupportedExceptionクラス
   * @param headers HttpHeadersクラス
   * @param status HttpStatusクラス
   * @param request WebRequestクラス
   * @return ResponseEntity<Object> json形式のレスポンス
   */
  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    log.warn(ex.getMessage(), ex);
    String errorLog = ex.getMessage();
    return handleExceptionInternal(
        ex,
        new ErrorResponse(
                messageSource.getMessage("error.products.notSupported.detail", null, Locale.JAPAN),
            errorLog),
        headers,
        status,
        request);
  }

  /**
   * 404 : URLが無効の場合に対応
   *
   * @param ex NoHandlerFoundExceptionクラス
   * @param headers HttpHeadersクラス
   * @param status HttpStatusクラス
   * @param request WebRequestクラス
   * @return ResponseEntity<Object> json形式のレスポンス
   */
  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(
      NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.warn(ex.getMessage(), ex);
    String errorLog = ex.getMessage();
    return handleExceptionInternal(
        ex,
        new ErrorResponse(
                messageSource.getMessage("error.products.nohandlefound", null, Locale.JAPAN), errorLog),
        headers,
        status,
        request);
  }
}
