package com.example.restfulapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * LogDTOクラス
 *
 * @author Natsume Takuya
 */
@Data
@Builder
public class LogContentsDto {
  String apiName;

  String httpMethod;

  int httpStatusCode;

  int accessCount;

  double executionTime;

  LocalDate date;
}
