package com.example.restfulapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * ProductDtoクラス
 *
 * @author Natsume Takuya
 */
@Data
@Builder
public class ProductDto {

  private BigInteger id;

  private String title;

  private String description;

  private int price;

  private String imagePath;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updateTime;
}
