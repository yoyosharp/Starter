package com.yoyodev.starter.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User extends AbstractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String password;

    private String username;

    private String email;

    private Timestamp verifiedAt;

    private Integer status;

    @ManyToMany
    @JoinTable(name = "user_has_permission",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions;
}
