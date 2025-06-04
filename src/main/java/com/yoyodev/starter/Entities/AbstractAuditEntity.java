package com.yoyodev.starter.Entities;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public class AbstractAuditEntity {
    @CreatedDate
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
