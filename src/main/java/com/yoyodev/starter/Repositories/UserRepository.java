package com.yoyodev.starter.Repositories;

import com.yoyodev.starter.Entities.User;
import com.yoyodev.starter.Model.DTO.UserPrincipal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
