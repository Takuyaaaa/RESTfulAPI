package com.example.restfulapi.config;

import com.example.restfulapi.interceptor.OauthInterceptor;
import com.example.restfulapi.repository.AccessTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.MappedInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * ログイン状態を確認するOauthInterceptorクラスのためのConfigurationクラス
 *
 * @author Natsume Takuya
 */
@Configuration
@RequiredArgsConstructor
public class InterceptorConfig {

  private final HttpSession httpSession;
  private final HttpServletRequest httpServletRequest;
  private final AccessTokenRepository accessTokenRepository;
  private final MessageSource messageSource;

  /**
   * OauthInterceptorクラスの設定を始めるメソッド
   *
   * @return OauthInterceptor OauthInterceptorクラス
   */
  @Bean
  public OauthInterceptor oauthInterceptor() {
    return new OauthInterceptor(
            httpSession, accessTokenRepository, httpServletRequest, messageSource);
  }

  /**
   * OauthInterceptorクラスの適用範囲を設定するメソッド
   *
   * @return MappedInterceptor MappedInterceptorクラス
   */
  @Bean
  public MappedInterceptor interceptor() {
    return new MappedInterceptor(
        //        new String[] {"/**"},
        new String[] {"/api/**"},
        new String[] {"/", "/github/**", "/logout", "/api-log/**"},
            oauthInterceptor());
  }
}
