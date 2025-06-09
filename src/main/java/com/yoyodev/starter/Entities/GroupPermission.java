package com.yoyodev.starter.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "group_permission", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"group_id", "permission_id"})
})
@Getter
@Setter
public class GroupPermission extends AbstractAuditEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    private int level; // 0 = none, 1 = read, 2 = read_write
}
