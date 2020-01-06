package com.example.restfulapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configurationクラス
 *
 * @author Natsume Takuya
 */
@Component
@ConfigurationProperties(prefix = "path")
@Data
public class PathConfig {

  private String image;
}
