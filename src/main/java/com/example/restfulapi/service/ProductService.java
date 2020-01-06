package com.example.restfulapi.service;

import com.example.restfulapi.config.PathConfig;
import com.example.restfulapi.constant.AcceptedSuffixes;
import com.example.restfulapi.dto.ProductDto;
import com.example.restfulapi.entity.Product;
import com.example.restfulapi.exception.BadRequestException;
import com.example.restfulapi.exception.NotFoundException;
import com.example.restfulapi.exception.UnsupportedMediaException;
import com.example.restfulapi.form.ProductForm;
import com.example.restfulapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviceクラス
 *
 * @author Natsume Takuya
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ResourceLoader resourceLoader;
  private final MessageSource messageSource;
  private final PathConfig pathConfig;

  /**
   * 更新日順で全Productを取得するメソッド
   *
   * @return List<ProductDTO> 現在保存されている全Product
   */
  public List<ProductDto> findAllOrderByUpdateTime() {

    return productRepository.findAllByOrderByUpdateTime().stream()
        .map(this::convertToProductDto)
        .collect(Collectors.toList());
  }

  /**
   * Productに対して部分一致のtitle検索を行うメソッド
   *
   * @param title 検索に用いる入力文字
   * @return List<ProductDTO> 検索にヒットしたProductのリスト
   */
  public List<ProductDto> findByTitleContainingOrderByUpdateTime(String title) {

    return productRepository.findByTitleContainingOrderByUpdateTime(title).stream()
        .map(this::convertToProductDto)
        .collect(Collectors.toList());
  }

  /**
   * idでProductを検索するメソッド
   *
   * @param id 検索に用いるid
   * @return Product 検索にヒットしたProduct
   */
  public Product findById(BigInteger id) {
    return productRepository
        .findById(id)
        .orElseThrow(
            () ->
                new NotFoundException(
                        messageSource.getMessage("error.products.notfound.code", null, Locale.JAPAN)));
  }

  /**
   * Productの登録を行うメソッド
   *
   * @param productForm ProductFormクラス
   * @return Product　新規登録されたProduct
   */
  public ProductDto registerProduct(ProductForm productForm) {

    String title = productForm.getTitle();
    String description = productForm.getDescription();
    int price = productForm.getPrice();

    if (isTitleDuplicated(title)) {
      throw new BadRequestException(
              messageSource.getMessage("error.products.duplication", null, Locale.JAPAN));
    }

    Product product = new Product();
    passProductInfoFromForm(product, productForm, title, description, price);
    return convertToProductDto(product);
  }

  /**
   * Productの更新を行うメソッド
   *
   * @param id 更新対象Productのid
   * @param productForm ProductFormクラス
   * @return Product 更新されたProduct
   */
  public ProductDto editProduct(BigInteger id, ProductForm productForm) {
    Product product = findById(id);

    String title = productForm.getTitle();
    String description = productForm.getDescription();
    int price = productForm.getPrice();

    passProductInfoFromForm(product, productForm, title, description, price);
    return convertToProductDto(product);
  }

  /**
   * 　ProductFormで受けたデータをProductに渡すメソッド
   *
   * @param product Productクラス
   * @param productForm ProductFormクラス
   * @param title 受け渡すtitle
   * @param description 受け渡すdescription
   * @param price 受け渡す price
   */
  private void passProductInfoFromForm(
      Product product, ProductForm productForm, String title, String description, int price) {
    product.setTitle(title);
    product.setDescription(description);
    product.setPrice(price);
    product.setImagePath(productForm.getImagePath());
    productRepository.save(product);
  }

  /**
   * Productの削除を行うメソッド
   *
   * @param id 削除対象Productのid
   */
  public void deleteProduct(String pathToImageDirectory, BigInteger id) throws IOException {
    Path pathToIdDirectory = Paths.get(pathToImageDirectory + id);
    deleteDirectoryIfExist(pathToIdDirectory);
    Product product = findById(id);
    productRepository.delete(product);
  }

  /**
   * 画像の更新を行うメソッド
   *
   * @param id 画像更新対象Productのid
   * @param imagePath 対応するimagePathとして保存されるpath
   */
  private void updateImagePath(BigInteger id, String imagePath) {
    Product product = findById(id);

    product.setImagePath(imagePath);
    productRepository.save(product);
  }

  /**
   * アップロードされた画像のvalidationを行うメソッド
   *
   * @param suffix 画像の拡張子
   */
  private void validateImage(String suffix) {

    if (!AcceptedSuffixes.isSuffixValid(suffix)) {
      throw new UnsupportedMediaException(
              messageSource.getMessage("error.products.image.suffix", null, Locale.JAPAN));
    }
  }
  /**
   * 同一のtitleを持つProductがすでにあればexceptionを投げるメソッド
   *
   * @param title validationに使用するtitle
   */
  private boolean isTitleDuplicated(String title) {
    return productRepository.findByTitle(title).isPresent();
  }

  /**
   * 画像の更新処理を行うメソッド
   *
   * @param id 画像更新対象ProductのId
   * @param productImage アップロードされた画像
   * @return ProductDTO imagePath更新後の該当Product
   */
  public ProductDto updateImage(BigInteger id, MultipartFile productImage) throws IOException {
    Product product = findById(id);
    try {
      // Nullの場合にはcatchしてハンドル
      String suffix = (productImage.getContentType()).split("/")[1].toLowerCase();
      validateImage(suffix);
    } catch (NullPointerException ex) {
      throw new BadRequestException(
              messageSource.getMessage("error.products.image.null", null, Locale.JAPAN));
    }

    // 存在しない場合に、directoryの作成
    createImageDirectory();

    // もしすでに画像がある場合には実体を削除
    String suffix = (productImage.getContentType()).split("/")[1].toLowerCase();
    Path pathToIdDirectory = cretePathToIdDirectory(pathConfig.getImage(), id);
    deleteDirectoryIfExist(pathToIdDirectory);
    Files.createDirectories(pathToIdDirectory);

    UUID uuid = UUID.randomUUID();
    Path pathToImageFile = Paths.get(pathConfig.getImage() + "/" + id + "/" + uuid + "." + suffix);
    productImage.transferTo(pathToImageFile);

    updateImagePath(id, "/api/products/" + id + "/images/" + uuid + "." + suffix);
    return convertToProductDto(product);
  }

  /** 画像を保存するimageDirectoryを作成するメソッド */
  private void createImageDirectory() throws IOException {
    Path pathToImageDirectory = Paths.get(pathConfig.getImage());
    Files.createDirectories(pathToImageDirectory);
  }

  /**
   * もし既存のディレクトリがすでに存在する場合にはその削除を行うメソッド
   *
   * @param path idDirectoryFileまでのpath
   */
  private void deleteDirectoryIfExist(Path path) throws IOException {
    File idDirectoryFile = new File(String.valueOf(path));

    if (idDirectoryFile.exists()) {
      File[] previousImage = idDirectoryFile.listFiles();
      for (int i = 0; i < Objects.requireNonNull(idDirectoryFile.list()).length; i++) {

        if (previousImage != null) {
          boolean isPreviousImageDeleted = previousImage[i].delete();
          if (!isPreviousImageDeleted) {
            throw new IOException();
          }
        }
      }
      boolean isIdDirectoryFileDeleted = idDirectoryFile.delete();
      if (!isIdDirectoryFileDeleted) {
        throw new IOException();
      }
    }
  }

  /**
   * 画像をバイト配列に変換するメソッド
   *
   * @param id 画像の格納されているidDirectory番号
   * @param name 画像のname
   * @param suffix 画像のsuffix
   * @return byte[] バイト配列に変換された画像データ
   * @throws IOException 画像処理時の例外を投げるIOExceptionクラス
   */
  private byte[] convertImageToByte(BigInteger id, String name, String suffix) throws IOException {
    Resource resource =
            resourceLoader.getResource(
            "classpath:" + "/imageDirectory/" + id + "/" + name + "." + suffix);
    try (InputStream image = resource.getInputStream()) {
      return IOUtils.toByteArray(image);
    } catch (IOException ex) {
      throw new IOException();
    }
  }

  /**
   * HTTPのヘッダーを生成するメソッド
   *
   * @param byteArray バイト配列に変換された画像データ
   * @return 生成されたHttpHeadersクラス
   */
  private HttpHeaders setHeaders(byte[] byteArray) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentLength(byteArray.length);

    return headers;
  }

  /**
   * 拡張子に応じて適切なMediaTypeを設定するメソッド
   *
   * @param headers ContentTypeの設定に用いるHttpHeadersクラス
   * @param suffix 変換対象画像のsuffix
   */
  private void adjustMediaType(HttpHeaders headers, String suffix) {
    AcceptedSuffixes sentSuffix = AcceptedSuffixes.valueOf(suffix);
    EnumSet<AcceptedSuffixes> jpegSuffixes =
        EnumSet.of(AcceptedSuffixes.jpg, AcceptedSuffixes.jpeg);
    EnumSet<AcceptedSuffixes> gifSuffixes = EnumSet.of(AcceptedSuffixes.gif);
    EnumSet<AcceptedSuffixes> pngSuffixes = EnumSet.of(AcceptedSuffixes.png);
    if (jpegSuffixes.contains(sentSuffix)) {
      headers.setContentType(MediaType.IMAGE_JPEG);
    } else if (gifSuffixes.contains(sentSuffix)) {
      headers.setContentType(MediaType.IMAGE_GIF);
    } else if (pngSuffixes.contains(sentSuffix)) {
      headers.setContentType(MediaType.IMAGE_PNG);
    }
  }

  /**
   * 画像を保存するID番号ディレクトリまでのpathを作成するメソッド
   *
   * @param pathToImageDirectory ID番号ディレクトリが入るImageDirectoryまでのpath
   * @param id 作成対象のIDディレクトリ番号
   * @return Path ID番号ディレクトリまでのpath
   */
  private Path cretePathToIdDirectory(String pathToImageDirectory, BigInteger id) {
    return Paths.get(pathToImageDirectory + "/" + id);
  }

  public HttpEntity<byte[]> getImage(BigInteger id, String name, String suffix) throws IOException {
    findById(id);
    byte[] byteArray = convertImageToByte(id, name, suffix);
    HttpHeaders headers = setHeaders(byteArray);
    adjustMediaType(headers, suffix);
    return new HttpEntity<>(byteArray, headers);
  }

  /**
   * ProductのデータをDTOクラスに受け渡すメソッド
   *
   * @param product データを渡すProductクラス
   * @return ProductDTO データを受けたProductDTOクラス
   */
  public ProductDto convertToProductDto(Product product) {
    return ProductDto.builder()
        .id(product.getId())
        .title(product.getTitle())
        .description(product.getDescription())
        .price(product.getPrice())
        .imagePath(product.getImagePath())
        .createTime(product.getCreateTime())
        .updateTime(product.getUpdateTime())
        .build();
  }
}
