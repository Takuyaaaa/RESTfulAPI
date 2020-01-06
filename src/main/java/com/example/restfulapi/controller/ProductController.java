package com.example.restfulapi.controller;

import com.example.restfulapi.config.PathConfig;
import com.example.restfulapi.dto.ProductDto;
import com.example.restfulapi.entity.Product;
import com.example.restfulapi.form.ProductForm;
import com.example.restfulapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * Controllerクラス
 *
 * @author Natsume Takuya
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductController {

  private final ProductService productService;
  private final PathConfig pathConfig;

  /**
   * Productの登録を行うメソッド
   *
   * @param productForm 入力を受けるProductFormクラス
   * @return ProductDTO 作成したProductのDTOクラス
   */
  @PostMapping("/api/products")
  @ResponseStatus(HttpStatus.CREATED)
  public ProductDto registerProduct(@Validated @RequestBody ProductForm productForm) {

    return productService.registerProduct(productForm);
  }

  /**
   * Productの取得を行うメソッド。Title引数(任意)を渡すと部分一致検索を行う
   *
   * @param title 検索に使う入力文字
   * @return List<ProductDTO> 全ProductのリストかTitleが部分一致のProductのリスト
   */
  @GetMapping("/api/products")
  @ResponseStatus(HttpStatus.OK)
  public List<ProductDto> searchProducts(@RequestParam(required = false) String title) {

    if (StringUtils.isBlank(title)) {
      return productService.findAllOrderByUpdateTime();
    } else {
      return productService.findByTitleContainingOrderByUpdateTime(title);
    }
  }

  /**
   * ProductをIDを元に取得するメソッド
   *
   * @param id 検索対象ProductのId
   * @return ProductDTO Idに該当するProductのDTOクラス
   */
  @GetMapping("/api/products/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ProductDto searchProductsById(@PathVariable("id") BigInteger id) {
    Product product = productService.findById(id);
    return productService.convertToProductDto(product);
  }

  /**
   * Productの更新を行うメソッド
   *
   * @param id 更新対象Productのid
   * @param productForm 入力を受けるProductFormクラス
   * @return ProductDTO 更新後ProductのDTOクラス
   */
  @PutMapping("/api/products/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ProductDto changeProduct(
      @PathVariable("id") BigInteger id, @Validated @RequestBody ProductForm productForm) {

    return productService.editProduct(id, productForm);
  }

  /**
   * Productの削除を行うメソッド
   *
   * @param id 削除対象ProductのId
   */
  @DeleteMapping("/api/products/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteProduct(@PathVariable("id") BigInteger id) throws IOException {

    productService.deleteProduct(pathConfig.getImage(), id);
  }

  /**
   * 画像の更新を行うメソッド
   *
   * @param id 画像更新対象ProducのId
   * @param productImage アップロードされる画像ファイル
   * @return ProductDTO 更新されたProductのDTOクラス
   */
  @PatchMapping("/api/products/{id}/images")
  @ResponseStatus(HttpStatus.OK)
  public ProductDto updateImage(
      @PathVariable("id") BigInteger id, @RequestParam("productImage") MultipartFile productImage)
      throws IOException {

    return productService.updateImage(id, productImage);
  }

  /**
   * 画像の取得を行うメソッド
   *
   * @param id 画像取得対象ProductのId
   * @param path 取得対象画像のpath
   * @param suffix 取得対象画像の拡張子
   * @return HttpEntity<byte[]> バイト配列に変換された画像ファイル
   */
  @GetMapping("/api/products/{id}/images/{path}.{suffix}")
  @ResponseStatus(HttpStatus.OK)
  public HttpEntity<byte[]> getImage(
      @PathVariable("id") BigInteger id,
      @PathVariable("path") String path,
      @PathVariable("suffix") String suffix)
      throws IOException {

    return productService.getImage(id, path, suffix);
  }
}
