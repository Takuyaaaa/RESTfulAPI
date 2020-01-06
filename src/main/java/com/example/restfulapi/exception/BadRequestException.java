package com.example.restfulapi.exception;

/**
 * BadRequestExceptionクラス
 *
 * @author Natsume Takuya
 */
public class BadRequestException extends RuntimeException {

  private static final long serialVersionUID = 444105504606092960L;

  public BadRequestException(String message) {
    super(message);
  }
}
