package com.example.restfulapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring bootアプリケーションのメインクラス
 *
 * @author Natsume Takuya
 */
@SpringBootApplication
@EnableScheduling
public class RestfulApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(RestfulApiApplication.class, args);
  }
}
