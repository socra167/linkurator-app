package com.team8.project2.domain.member.service;

import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.standard.util.Ut;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private static final Logger log = LoggerFactory.getLogger(AuthTokenService.class);
    @Value("${custom.jwt.secret-key}")
    private String keyString;

    @Value("${custom.jwt.expire-seconds}")
    private int expireSeconds;

    @Transactional
    public String genAccessToken(Member member) {
        SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());
        String a = Ut.Jwt.createToken(
                keyString,
                expireSeconds,
                Map.of(
                        "id", member.getId(),
                        "memberId", member.getMemberId()
                )
        );
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseClaimsJws(a);

        Date expiration = claimsJws.getBody().getExpiration();
        log.info("[body expiration]"+expiration);

        return a;
    }

    public Map<String, Object> getPayload(String token) {
        if (!Ut.Jwt.isValidToken(keyString, token)) return null;

        Map<String, Object> payload = Ut.Jwt.getPayload(keyString, token);
        Number idNo = (Number) payload.get("id");
        long id = idNo.longValue();
        String memberId = (String) payload.get("memberId");
        return Map.of(
                "id", id,
                "memberId", memberId
        );
    }
}
