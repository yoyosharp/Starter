package com.yoyodev.starter.Entities.Projection;

import java.sql.Timestamp;

public interface UserAuthProjection {

    Long getUserId();

    String getUsername();

    Integer getUserStatus();

    Timestamp getVerifiedAt();

    String getModuleId();

    String getFunctionId();

    Integer getPermissionLevel();

    String getPermissionName();

    Integer getEnabledFlag();
}
