package com.example.restfulapi.exception;

/**
 * NotFoundExceptionクラス
 *
 * @author Natsume Takuya
 */
public class NotFoundException extends RuntimeException {
  private static final long serialVersionUID = -7000582584160745443L;

  public NotFoundException(String message) {
    super(message);
  }
}
