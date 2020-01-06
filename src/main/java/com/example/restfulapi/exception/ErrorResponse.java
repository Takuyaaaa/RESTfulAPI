package com.example.restfulapi.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * ErrorResponseクラス
 *
 * @author Natsume Takuya
 */
@Value
class ErrorResponse {

  @JsonProperty("Error")
  private Error error;

  ErrorResponse(String detail, String code) {
    error = new Error(detail, code);
  }

  @Value
  private static class Error {

    @JsonProperty("Detail")
    private final String detail;

    @JsonProperty("Code")
    private final String code;
  }
}
