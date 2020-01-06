package com.example.restfulapi.interceptor;

import com.example.restfulapi.exception.UnauthorizedException;
import com.example.restfulapi.repository.AccessTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * Interceptorクラス
 *
 * @author Natsume Takuya
 */
@RequiredArgsConstructor
public class OauthInterceptor extends HandlerInterceptorAdapter {

  private final HttpSession httpSession;
  private final AccessTokenRepository accessTokenRepository;
  private final HttpServletRequest httpServletRequest;
  private final MessageSource messageSource;

  /**
   * ユーザーのログイン状態を評価する
   *
   * @param httpSession HttpSessionクラス
   * @return boolean ログインをしている状態であればtrue、そうでなければfalse
   */
  private boolean isUserLoggedIn(HttpSession httpSession, String sentToken) {
    if (httpSession.getAttribute("token") != null) {
      return true;
    } else if ((accessTokenRepository
            .findByAccessToken(sentToken.replace("Bearer ", ""))
            .isPresent()
        && sentToken.contains("Bearer "))) {
      return true;
    } else {
      throw new UnauthorizedException(
              messageSource.getMessage("error.products.unauthorized.code", null, Locale.JAPAN));
    }
  }

  /**
   * Controller実行前にログイン確認処理を行う
   *
   * @param request HttpServletRequestクラス
   * @param response HttpServletResponseクラス
   * @param handler Objectクラス
   * @return boolean もしログインをしている状態であればtrue, そうでなければエラーに対応するページを表示
   */
  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (httpServletRequest.getHeader("Authorization") == null) {
      throw new UnauthorizedException(
              messageSource.getMessage("error.products.unauthorized.code", null, Locale.JAPAN));
    }
    String sentToken = httpServletRequest.getHeader("Authorization");
    return handler instanceof ResourceHttpRequestHandler || isUserLoggedIn(httpSession, sentToken);
  }
}
