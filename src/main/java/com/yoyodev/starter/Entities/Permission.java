package com.yoyodev.starter.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "permission", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"moduleId", "functionId"})
})
public class Permission extends AbstractAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String moduleId;
    private String functionId;
    private int enabledFlag; // 0 = disabled, 1 = enabled
}
