package com.team8.project2.global;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedisConnection() {
        redisTemplate.opsForValue().set("testKey", "Hello Redis");
        String value = redisTemplate.opsForValue().get("testKey");
        System.out.println("Redis에서 가져온 값: " + value);

        // Assert문을 추가하여 테스트 결과를 검증할 수 있습니다.
        assert value != null;
        assert value.equals("Hello Redis");
    }
}
