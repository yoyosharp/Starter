package com.yoyodev.starter.Repositories;

import com.yoyodev.starter.Entities.Projection.UserAuthProjection;
import com.yoyodev.starter.Entities.Projection.UserProjection;
import com.yoyodev.starter.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<User, Long> {

    @Query("""
            SELECT u FROM User u
            WHERE u.username = :identity
            OR u.email = :identity
            """)
    Optional<UserProjection> findUserByIdentity(@Param("identity") String identity);

    @Query(value = "SELECT * FROM get_user_auth(:username)", nativeQuery = true)
    List<UserAuthProjection> findUserAuthByUsername(@Param("username") String username);
}
