package com.yoyodev.starter.Repositories;

import com.yoyodev.starter.Entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findAccessTokenByUserId(Long userId);
}