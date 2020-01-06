package com.example.restfulapi.repository;

import com.example.restfulapi.entity.LogContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

/**
 * ApiLogのRepositoryクラス
 *
 * @author Natsume Takuya
 */
@Repository
public interface LogContentsRepository extends JpaRepository<LogContents, BigInteger> {

  List<LogContents> findByFileName(String fileName);

  @Query(
      "SELECT apiName,httpMethod,httpStatusCode,SUM(accessCount) ,AVG(executionTime) FROM LogContents WHERE date BETWEEN ?1 AND ?2 GROUP BY apiName,httpMethod,httpStatusCode")
  List<String> findByDateAndReCalculate(LocalDate startDate, LocalDate endDate);
}
