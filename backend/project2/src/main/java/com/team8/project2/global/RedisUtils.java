package com.team8.project2.global;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisUtils {

	private final RedisTemplate<String, Object> redisTemplate;

	public void clearAllData() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();
	}
}
