package com.yoyodev.starter.Repositories;

import com.yoyodev.starter.Entities.User;
import com.yoyodev.starter.Model.DTO.UserPrincipal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<User, Long>, UserRepository {
    Optional<UserPrincipal> findUserAuthById(Long id);

    Optional<UserPrincipal> findUserPrincipalByUsername(String username);

    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.permissions r
            WHERE u.username = ?1
            OR u.email = ?1
            """)
    Optional<UserPrincipal> findUserAuthByIdentity(String identity);
}
