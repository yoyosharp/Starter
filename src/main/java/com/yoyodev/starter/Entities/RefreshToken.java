package com.yoyodev.starter.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "refresh_token", uniqueConstraints = {
        @UniqueConstraint(name = "uc_refreshtoken_userid", columnNames = {"userId"})
})
@Getter
@Setter
public class RefreshToken extends AbstractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String token;

    @Column
    private Timestamp expirationTime;

    @Column
    private Long userId;
}
