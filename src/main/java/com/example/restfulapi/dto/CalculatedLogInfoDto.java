package com.example.restfulapi.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * CalculatedLogInfoDtoクラス
 *
 * @author Natsume Takuya
 */
@Data
public class CalculatedLogInfoDto {

  LocalDate startLocalDate;

  LocalDate endLocalDate;

  List<LogContentsDto> logContentsDtoList;
}
