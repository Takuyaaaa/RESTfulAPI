package com.example.restfulapi.dto;

import lombok.Builder;
import lombok.Data;

/**
 * ProductDtoクラス
 *
 * @author Natsume Takuya
 */
@Data
@Builder
public class UserInfoDto {

  private String userImage;

  private long userId;

  private String userCompany;

  private String userName;

  private String userEmail;
}
