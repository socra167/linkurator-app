package com.team8.project2.standard.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class Ut {
    private static final Logger log = LoggerFactory.getLogger(Ut.class);

    public static class Json {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        public static String toString(Object obj) {
            try {
                return objectMapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Jwt {
        public static String createToken(String keyString, int expireSeconds, Map<String, Object> claims) {
            SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());

            Date issuedAt = new Date();
            Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);
            System.out.println("[JVM 시간대] " + TimeZone.getDefault().getID());
            log.info("[expiration]"+expiration);
            log.info("[issuedAt]"+issuedAt.toString());

            return Jwts.builder()
                    .claims(claims)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .signWith(secretKey)
                    .compact();
        }

        public static boolean isValidToken(String keyString, String token) {
            long currentTime = System.currentTimeMillis();
            System.out.println("[현재 서버 시간] " + new Date(currentTime));
            try {
                SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());

                Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parse(token);


                return true; // 토큰이 정상적으로 검증되면 true 반환
            } catch (ExpiredJwtException e) {
                System.out.println("[JWT] 토큰 만료됨: " + e.getMessage());
            } catch (SignatureException e) {
                System.out.println("[JWT] 서명 불일치: " + e.getMessage());
            } catch (MalformedJwtException e) {
                System.out.println("[JWT] 형식 오류: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[JWT] 기타 오류: " + e.getMessage());
            }
            return false;
        }

        public static Map<String, Object> getPayload(String keyString, String jwtStr) {
            SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());

            return (Map<String, Object>) Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parse(jwtStr)
                    .getPayload();
        }
    }
}
