package com.example.restfulapi.exception;

/**
 * UnsupportedMediaExceptionクラス
 *
 * @author Natsume Takuya
 */
public class UnsupportedMediaException extends RuntimeException {

  private static final long serialVersionUID = 5666882358142815737L;

  public UnsupportedMediaException(String message) {
    super(message);
  }
}
