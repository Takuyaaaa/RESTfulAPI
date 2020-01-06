package com.example.restfulapi.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * AccessTokenのEntityクラス
 *
 * @author Natsume Takuya
 */
@Entity
@Table(name = "access_token")
@Data
public class AccessToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  BigInteger id;

  String accessToken;

  @CreationTimestamp private LocalDateTime createTime;

  @UpdateTimestamp private LocalDateTime updateTime;
}
