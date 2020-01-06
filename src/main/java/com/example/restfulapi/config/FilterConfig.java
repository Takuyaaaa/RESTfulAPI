package com.example.restfulapi.config;

import com.example.restfulapi.filter.TokenFilter;
import com.example.restfulapi.logger.BatchLogger;
import com.example.restfulapi.repository.AccessTokenRepository;
import com.example.restfulapi.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

/**
 * TokenFilterクラスのConfigurationクラス
 *
 * @author Natsume Takuya
 */
@Configuration
@RequiredArgsConstructor
public class FilterConfig {

  private final HttpServletRequest httpServletRequest;
  private final AccessTokenRepository accessTokenRepository;
  private final OauthService oauthService;

  /**
   * TokenFilterの設定を行うメソッド
   *
   * @return FilterRegistrationBean FilterRegistrationBeanクラス
   */
  @Bean
  public FilterRegistrationBean<TokenFilter> tokenFilter() {
    FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(
        new TokenFilter(httpServletRequest, accessTokenRepository, oauthService));
    registrationBean.addUrlPatterns("/api/*");

    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<BatchLogger> batchLogger() {
    FilterRegistrationBean<BatchLogger> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new BatchLogger());
    registrationBean.addUrlPatterns("/api/*");

    return registrationBean;
  }
}
