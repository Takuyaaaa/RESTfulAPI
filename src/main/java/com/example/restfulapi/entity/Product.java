package com.example.restfulapi.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * ProductのEntityクラス
 *
 * @author Natsume Takuya
 */
@Entity
@Table(name = "products")
@Data
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  BigInteger id;

  String title;

  String description;

  int price;

  String imagePath;

  @CreationTimestamp private LocalDateTime createTime;

  @UpdateTimestamp private LocalDateTime updateTime;
}
