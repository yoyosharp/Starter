package com.yoyodev.starter.Service;

import java.util.List;

public interface RedisCacheService {
    void saveOne(String key, String value, int expireInSeconds);

    <T> T getOne(String key, Class<T> clazz);

    <T> List<T> getList(String key, Class<T> clazz);

    void deleteOne(String key);

    boolean existsByKey(String key);
}
