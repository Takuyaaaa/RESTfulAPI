package com.example.restfulapi.filter;

import com.example.restfulapi.entity.AccessToken;
import com.example.restfulapi.repository.AccessTokenRepository;
import com.example.restfulapi.service.OauthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * API使用の際、トークン処理を行うFilterクラス
 *
 * @author Natsume Takuya
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenFilter implements Filter {

  private final HttpServletRequest httpServletRequest;
  private final AccessTokenRepository accessTokenRepository;
  private final OauthService oauthService;

  /**
   * Filterクラスの初期化を行うメソッド
   *
   * @param filterConfig FilterConfigクラス
   */
  @Override
  public void init(FilterConfig filterConfig) {
    oauthService.deleteTokenCreatedBeforeBoarderLine();
    log.info("Filter was successfully initialized");
  }

  /**
   * Filterで行う内容を記述するメソッド
   *
   * @param request ServletRequestクラス
   * @param response ServletResponseクラス
   * @param chain FilterChainクラス
   * @throws IOException IOExceptionクラス
   * @throws ServletException ServletExceptionクラス
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String sentToken = httpServletRequest.getHeader("Authorization");
    Optional searchResult =
            accessTokenRepository.findByAccessToken(sentToken.replace("Bearer ", ""));
    if ((searchResult.isPresent() && sentToken.contains("Bearer "))) {
      AccessToken accessToken = (AccessToken) searchResult.get();
      accessToken.setUpdateTime(LocalDateTime.now());
      accessTokenRepository.save(accessToken);
    } else {
      log.warn("Access Token sent for API is not valid");
    }

    chain.doFilter(request, response);
  }
}
