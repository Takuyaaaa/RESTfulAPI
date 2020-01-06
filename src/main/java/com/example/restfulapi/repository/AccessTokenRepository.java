package com.example.restfulapi.repository;

import com.example.restfulapi.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Repositoryクラス
 *
 * @author Natsume Takuya
 */
@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, BigInteger> {

  Optional<AccessToken> findByAccessToken(String accessToken);
}
