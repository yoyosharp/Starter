package com.yoyodev.starter.Repositories;

import com.yoyodev.starter.Entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findAccessTokenByUserId(Long userId);

  @Query("SELECT MIN(userId) FROM RefreshToken")
  Long getMinUserId();
}