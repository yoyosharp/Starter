package com.yoyodev.starter.AOP.Cache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisCacheService {
    private final RedisTemplate<String, String> redisTemplate;
    private final Gson gson;

    public RedisCacheService(RedisTemplate<String, String> redisTemplate, @Qualifier(value = "redis-serializer") Gson gson) {
        this.redisTemplate = redisTemplate;
        this.gson = gson;
    }

    public void saveOne(String key, Object value, int expireInSeconds) {
        try {
            String json = gson.toJson(value);
            redisTemplate.opsForValue().set(key, json, expireInSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to save to Redis, key: {}", key, e);
        }
    }

    public <T> T getOne(String key, Class<T> clazz) {
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            return gson.fromJson(value, clazz);
        } catch (Exception e) {
            log.error("Error getting from Redis cache: {}", e.getMessage(), e);
            return null;
        }
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return new ArrayList<>();
            }
            return gson.fromJson(value, new TypeToken<List<T>>() {}.getType());
        } catch (Exception e) {
            log.error("Error getting list from Redis cache: {}", e.getMessage(), e);
            return null;
        }
    }

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
