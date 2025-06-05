package com.yoyodev.starter.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "role_permission", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"role_id", "permission_id"})
})
public class RolePermission extends AbstractAuditEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    private int level; // 0 = none, 1 = read, 2 = read_write
}

