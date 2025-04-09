package com.team8.project2.global

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisUtils(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    // flushAll() 메서드를 사용하여 Redis DB의 모든 데이터를 삭제합니다.
    fun clearAllData() {
        redisTemplate.connectionFactory?.connection?.let {
            it.flushAll()
            println("Redis DB flushed.")
        } ?: println("Redis connection is null. Cannot flush.")
    }

}
