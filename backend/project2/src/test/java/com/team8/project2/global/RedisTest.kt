package com.team8.project2.global

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate
import org.assertj.core.api.Assertions.assertThat

@SpringBootTest
class RedisTest {

    @Autowired
    lateinit var redisTemplate: StringRedisTemplate

    @Test
    fun testRedisConnection() {
        redisTemplate.opsForValue().set("testKey", "Hello Redis")
        val value = redisTemplate.opsForValue().get("testKey")
        println("Redis에서 가져온 값: $value")

        assertThat(value).isEqualTo("Hello Redis")
    }
}
