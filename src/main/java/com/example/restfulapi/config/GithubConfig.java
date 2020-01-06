package com.example.restfulapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Github OAuth接続のためのJVM Optionsを読み込むConfigurationクラス
 *
 * @author Natsume Takuya
 */
@Component
@ConfigurationProperties(prefix = "github")
@Data
public class GithubConfig {

  private String clientId;

  private String clientSecret;

  private String callbackUrl;
}
