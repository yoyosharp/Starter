package com.yoyodev.starter.Service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yoyodev.starter.Service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseRedisCacheService implements RedisCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Gson gson = new Gson();

    @Override
    public void saveOne(String key, String value, int expireInSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, expireInSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to save to Redis, key: {}", key, e);
        }
    }

    @Override
    public <T> T getOne(String key, Class<T> clazz) {
        try {
            String value = (String) redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            return gson.fromJson(value, clazz);
        } catch (Exception e) {
            log.error("Error getting from Redis cache: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        try {
            String value = (String) redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            return gson.fromJson(value, new TypeToken<List<T>>() {}.getType());
        } catch (Exception e) {
            log.error("Error getting list from Redis cache: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void deleteOne(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Error deleting from Redis cache: {}", e.getMessage(), e);
        }
    }

    public boolean existsByKey(String key) {
        return redisTemplate.hasKey(key);
    }
}
