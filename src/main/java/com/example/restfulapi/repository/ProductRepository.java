package com.example.restfulapi.repository;

import com.example.restfulapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * Repositoryクラス
 *
 * @author Natsume Takuya
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, BigInteger> {

  /**
   * 登録されているProductを更新日順に取得するメソッド
   *
   * @return List<Product> 登録されているProductのリスト
   */
  List<Product> findAllByOrderByUpdateTime();

  /**
   * Title部分一致でProductを更新日順に取得するメソッド
   *
   * @param title 検索に用いる入力title
   * @return List<Product> 検索にかかったProductのリスト
   */
  List<Product> findByTitleContainingOrderByUpdateTime(String title);

  /**
   * titleでProductを取得するメソッド
   *
   * @param title Productのtitle
   * @return Optional<Product> 該当titleのProduct
   */
  Optional<Product> findByTitle(String title);
}
