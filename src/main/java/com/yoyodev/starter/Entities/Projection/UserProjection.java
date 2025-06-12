package com.yoyodev.starter.Entities.Projection;

import java.sql.Timestamp;

public interface UserProjection {
    Long getId();

    String getUsername();

    String getPassword();

    Integer getStatus();

    Timestamp getVerifiedAt();

}
