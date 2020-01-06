package com.example.restfulapi.exception;

/**
 * UnauthorizedExceptionクラス
 *
 * @author Natsume Takuya
 */
public class UnauthorizedException extends RuntimeException {

  private static final long serialVersionUID = 8074925309829970579L;

  public UnauthorizedException(String message) {
    super(message);
  }
}
