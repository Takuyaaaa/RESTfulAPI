package com.example.restfulapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Access Token有効期限を管理するconfigurationクラス
 *
 * @author Natsume Takuya
 */
@Component
@ConfigurationProperties(prefix = "token")
@Data
public class TokenSessionConfig {

  private int boarderLine;
}
