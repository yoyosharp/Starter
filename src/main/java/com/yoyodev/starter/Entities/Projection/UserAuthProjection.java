package com.yoyodev.starter.Entities.Projection;

import java.sql.Timestamp;

public interface UserAuthProjection {

    Long getUserId();

    String getUsername();

    Integer getStatus();

    Timestamp getVerifiedAt();

    String getModuleId();

    String getFunctionId();

    Integer getLevel();

    Integer getEnabledFlag();
}
