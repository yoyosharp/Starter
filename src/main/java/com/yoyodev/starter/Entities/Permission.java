package com.yoyodev.starter.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    @Id
    private Integer id;

    private String name;

    private String module;

    private String functionName;

    // 0 = none, 1 = get, 2 = get & update
    private Integer level;

    private Integer enabled;

}
