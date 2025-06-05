package com.yoyodev.starter.Repositories;

import com.yoyodev.starter.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, AuthUserRepository {
}
