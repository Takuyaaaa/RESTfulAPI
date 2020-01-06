package com.example.restfulapi.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;

/**
 * ApiLogのEntityクラス
 *
 * @author Natsume Takuya
 */
@Entity
@Table(name = "api_log")
@Data
public class LogContents {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  BigInteger id;

  String apiName;

  String httpMethod;

  int httpStatusCode;

  int accessCount;

  double executionTime;

  LocalDate date;

  String fileName;
}
