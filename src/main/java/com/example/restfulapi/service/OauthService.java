package com.example.restfulapi.service;

import com.example.restfulapi.config.GithubConfig;
import com.example.restfulapi.config.TokenSessionConfig;
import com.example.restfulapi.dto.UserInfoDto;
import com.example.restfulapi.entity.AccessToken;
import com.example.restfulapi.repository.AccessTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

/**
 * Serviceクラス
 *
 * @author Natsume Takuya
 */
@Service
@Transactional
@RequiredArgsConstructor
public class OauthService {

  private final GithubConfig githubConfig;
  private final TokenSessionConfig tokenSessionConfig;
  private final AccessTokenRepository accessTokenRepository;

  /**
   * AccessTokenの作成・保存・削除を行う
   *
   * @param generatedToken AccessToken用に生成されたトークン
   */
  public void processAccessToken(String generatedToken) {
    deleteTokenCreatedBeforeBoarderLine();
    AccessToken accessToken = new AccessToken();
    accessToken.setAccessToken(generatedToken);
    accessTokenRepository.save(accessToken);
  }

  /**
   * oauthTokenを取得する
   *
   * @param code OrAuth処理で生成されたコード
   * @return String 取得したOAuthToken
   */
  public String getOauthToken(String code) {
    return operations()
        .exchangeForAccess(code, githubConfig.getCallbackUrl(), null)
        .getAccessToken();
  }

  /**
   * accessToken用のトークン生成を行う
   *
   * @return String 生成されたトークン
   */
  public String generateToken() {
    UUID uuid = UUID.randomUUID();
    Charset charset = StandardCharsets.UTF_8;
    return Base64.getEncoder().encodeToString(uuid.toString().getBytes(charset));
  }

  /** 30分前より以前に作成されたAPI用のアクセストークンをDBから一括削除 */
  public void deleteTokenCreatedBeforeBoarderLine() {
    accessTokenRepository.findAll().stream()
        .filter(
            item ->
                item.getUpdateTime()
                    .isBefore(
                        LocalDateTime.now().minusMinutes(tokenSessionConfig.getBoarderLine())))
        .forEach(accessTokenRepository::delete);
  }

  /**
   * github認証を行う
   *
   * @return OAuth2Operations OAuth2の処理を行うインターフェース
   */
  public OAuth2Operations operations() {
    GitHubConnectionFactory gitHubConnectionFactory =
        new GitHubConnectionFactory(githubConfig.getClientId(), githubConfig.getClientSecret());
    return gitHubConnectionFactory.getOAuthOperations();
  }

  /**
   * ユーザー情報をDTOクラスに受け渡す
   *
   * @param userInfo アクセストークンから取得したユーザーinfo
   * @return UserInfoDto DTOクラス
   */
  public UserInfoDto passUserInfoToDto(String userInfo) {
    GitHub gitHub = new GitHubTemplate(userInfo);
    GitHubUserProfile gitHubUser = gitHub.userOperations().getUserProfile();

    return UserInfoDto.builder()
        .userImage(gitHubUser.getProfileImageUrl())
        .userId(gitHubUser.getId())
        .userCompany(gitHubUser.getCompany())
        .userName(gitHubUser.getUsername())
        .userEmail(gitHubUser.getEmail())
        .build();
  }
}
