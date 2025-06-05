package com.yoyodev.starter.Repositories;

import com.yoyodev.starter.Entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository {

    @Query("""
            SELECT u FROM User u
            LEFT JOIN FETCH u.userPermissions up
            LEFT JOIN FETCH up.permission
            LEFT JOIN FETCH u.userRoles ur
            LEFT JOIN FETCH ur.role r
            LEFT JOIN FETCH r.rolePermissions rp
            LEFT JOIN FETCH rp.permission
            WHERE u.username = :identity
            OR u.email = :identity
            """)
    Optional<User> findUserAuthByIdentity(@Param("identity") String identity);
}
