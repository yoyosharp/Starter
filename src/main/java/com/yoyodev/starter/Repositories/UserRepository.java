package com.yoyodev.starter.Repositories;

import com.yoyodev.starter.Entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends AuthUserRepository {

    /**
     * Find a user by email.
     * 
     * @param email The user's email
     * @return An Optional containing the User if found
     */
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
}
