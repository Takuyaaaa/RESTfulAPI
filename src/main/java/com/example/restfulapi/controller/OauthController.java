package com.example.restfulapi.controller;

import com.example.restfulapi.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * Controllerクラス
 *
 * @author Natsume Takuya
 */
@Controller
@RequiredArgsConstructor
public class OauthController {

  private static final String TOKEN = "token";
  private final HttpSession httpSession;
  private final OauthService oauthService;

  /**
   * トップページを表示するindexメソッド
   *
   * @param modelAndView ModelAndViewクラス
   * @return ModelAndView トップ画面を表示
   */
  @GetMapping("/")
  public ModelAndView index(ModelAndView modelAndView) {

    modelAndView.setViewName("index");
    modelAndView.addObject("token", httpSession.getAttribute("token"));
    return modelAndView;
  }

  /**
   * Github ログインを実行するメソッド
   *
   * @return String callback実行に対応するURL
   */
  @GetMapping("/github/login")
  public String login() {
    return "redirect:"
        + oauthService
            .operations()
            .buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, new OAuth2Parameters());
  }

  /**
   * Github callbackを実行するメソッド
   *
   * @param code アクセストークン取得に用いるcode
   * @return String Githubプロフィールに対応するURL
   */
  @GetMapping("/github/callback")
  public String callback(@RequestParam String code) {
    String oauthToken = oauthService.getOauthToken(code);
    String generatedToken = oauthService.generateToken();
    oauthService.processAccessToken(generatedToken);
    httpSession.setAttribute(TOKEN, oauthToken);
    httpSession.setAttribute("accessToken", generatedToken);
    return "redirect:/github";
  }

  /**
   * Githubプロフィールを表示するメソッド
   *
   * @param modelAndView ModelAndViewクラス
   * @return ModelAndView Githubプロフィール画面を表示
   */
  @GetMapping("/github")
  public ModelAndView profile(ModelAndView modelAndView) {
    String userInfo = (String) httpSession.getAttribute(TOKEN);
    modelAndView.setViewName("profile");
    modelAndView.addObject("token", httpSession.getAttribute("accessToken"));
    if (userInfo != null) {
      modelAndView.addObject("userInfoDto", oauthService.passUserInfoToDto(userInfo));
    } else {
      modelAndView.setViewName("error/401");
    }
    return modelAndView;
  }

  /**
   * Github logoutを実行するメソッド
   *
   * @return String トップページにリダイレクト
   */
  @GetMapping("/logout")
  public String logout() {
    httpSession.invalidate();
    return "redirect:/";
  }
}
