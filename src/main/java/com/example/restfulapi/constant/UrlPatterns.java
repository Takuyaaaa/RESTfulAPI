package com.example.restfulapi.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * APIとそれに対応するURLパターン・Httpメソッドを持つEnumクラス
 *
 * @author Natsume Takuya
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public enum UrlPatterns {
  REGISTER_PRODUCT("^/api/products?$", "POST", "商品登録API"),
  SEARCH_PRODUCT("^/api/products/?$", "GET", "商品取得API(複数件)"),
  SEARCH_PRODUCTS_BY_TITLE("^/api/products\\?title=.*$", "GET", "商品取得API（１件）"),
  SEARCH_PRODUCTS_BY_ID("^(/api/products/)([0-9]+)$", "PUT", "商品更新API"),
  CHANGE_PRODUCT("^(/api/products/)([0-9]+)$", "DELETE", "商品削除API"),
  DELETE_PRODUCT("^(/api/products/)([0-9]+)$", "PATCH", "商品画像更新API"),
  UPDATE_IMAGE("^(/api/products/)([0-9]+)(/images)$", "PATCH", "商品画像更新API"),
  GET_IMAGE(
      "^(/api/products/)([0-9]+)(/images/)([0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12})(.jpeg|.jpg|.png|.gif)$",
      "GET", "商品画像取得API");

  private final String urlPattern;
  private final String httpMethod;
  private final String apiName;

  /**
   * URLとHttpメソッドからAPI名を割り当てる
   *
   * @param apiName 記録されているurl
   * @param httpMethod 記録されているHttpMethod
   * @return utlとHttpMethodから割り当てられるAPI名
   */
  public static String checkApiName(String apiName, String httpMethod) {
    if (!StringUtils.isBlank(apiName)) {
      for (UrlPatterns url : UrlPatterns.values()) {
        if (Pattern.compile((url.getUrlPattern())).matcher(apiName).find()
            && Pattern.compile(url.getHttpMethod()).matcher(httpMethod).find()) {
          return url.getApiName();
        }
      }
    }
    return apiName;
  }
}
