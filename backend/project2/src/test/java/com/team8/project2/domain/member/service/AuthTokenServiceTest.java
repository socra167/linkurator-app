package com.team8.project2.domain.member.service;

import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.standard.util.Ut;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthTokenServiceTest {

    @Autowired
    private AuthTokenService authTokenService;
    @Autowired
    private MemberService memberService;

    @Value("${custom.jwt.secret-key}")
    private String keyString;

    @Value("${custom.jwt.expire-seconds}")
    private int expireSeconds;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = memberService.findByMemberId("memberId")
                .orElseThrow(() -> new IllegalArgumentException("user1 not found"));
    }

    @Test
    @DisplayName("jwt access token 생성 및 페이로드 검증")
    void createAccessToken() {
        // Access Token 생성
        String accessToken = authTokenService.genAccessToken(testMember);

        assertThat(accessToken).isNotBlank();

        // Payload 검증
        Map<String, Object> parsedPayload = authTokenService.getPayload(accessToken);
        assertThat(parsedPayload).containsEntry("id", testMember.getId());
        assertThat(parsedPayload).containsEntry("memberId", testMember.getMemberId());

        System.out.println("AccessToken = " + accessToken);
    }

    @Test
    @DisplayName("jwt access token 유효성 검사")
    void validateAccessToken() {
        // 토큰 생성
        String accessToken = authTokenService.genAccessToken(testMember);

        // 토큰이 유효한지 확인
        boolean isValid = Ut.Jwt.isValidToken(keyString, accessToken);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("jwt 만료된 토큰 검사")
    void expiredTokenCheck() throws InterruptedException {
        // 테스트용 만료 시간 1초로 설정 후 토큰 생성
        String shortLivedToken = Ut.Jwt.createToken(keyString, 1, Map.of("id", testMember.getId()));

        // 2초 대기하여 토큰 만료
        Thread.sleep(2000);

        // 만료된 토큰인지 확인
        boolean isValid = Ut.Jwt.isValidToken(keyString, shortLivedToken);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("잘못된 토큰 검증")
    void invalidTokenCheck() {
        String invalidToken = "this.is.a.fake.token";

        // 잘못된 토큰은 유효하지 않아야 함
        boolean isValid = Ut.Jwt.isValidToken(keyString, invalidToken);
        assertThat(isValid).isFalse();
    }

}