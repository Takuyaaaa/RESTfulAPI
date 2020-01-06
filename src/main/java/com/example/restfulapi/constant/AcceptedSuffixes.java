package com.example.restfulapi.constant;

/**
 * 登録可能な拡張子をまとめたenum変数
 *
 * @author Natsume Takuya
 */
public enum AcceptedSuffixes {
  jpeg,
  jpg,
  png,
  gif;

  /**
   * アップロードされた画像の拡張子が有効でない場合にexceptionを投げるメソッド
   *
   * @param suffix validationに使用するsuffix
   */
  public static boolean isSuffixValid(String suffix) {
    AcceptedSuffixes sentSuffix = valueOf(suffix);

    for (AcceptedSuffixes acceptedSuffix : AcceptedSuffixes.values()) {
      if (sentSuffix == acceptedSuffix) {
        return true;
      }
    }
    return false;
  }
}
