package com.example.restfulapi.form;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * ProductFormクラス
 *
 * @author Natsume Takuya
 */
@Data
public class ProductForm {

  @NotEmpty(message = "{error.products.title.empty}")
  @Size(max = 100, message = "{error.products.title.size}")
  String title;

  @NotEmpty(message = "{error.products.description.empty}")
  @Size(max = 500, message = "{error.products.description.size}")
  String description;

  @Min(value = 1, message = "{error.products.price}")
  @Max(value = 1000000, message = "{error.products.price}")
  int price;

  String imagePath;
}
