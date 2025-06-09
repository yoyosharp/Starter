package com.yoyodev.starter.Repositories;

import com.yoyodev.starter.Entities.Projection.UserAuthProjection;
import com.yoyodev.starter.Entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthUserRepository {

    @EntityGraph(attributePaths = {
            "userPermissions.permission",
            "userRoles.role.rolePermissions.permission",
            "groupUsers.group.groupPermissions.permission"
    })
    @Query("""
            SELECT DISTINCT u FROM User u
            WHERE u.username = :identity
            OR u.email = :identity
            """)
    Optional<User> findUserByIdentity(@Param("identity") String identity);

    @Query(value = "SELECT * FROM get_user_auth(:identity)", nativeQuery = true)
    List<UserAuthProjection> findUserAuthByIdentity(String identity);
}
