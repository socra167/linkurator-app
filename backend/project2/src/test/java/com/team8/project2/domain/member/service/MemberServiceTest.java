package com.team8.project2.domain.member.service;

import com.team8.project2.domain.curation.curation.repository.CurationRepository;
import com.team8.project2.domain.image.service.S3Uploader;
import com.team8.project2.domain.member.dto.FollowResDto;
import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.domain.member.entity.RoleEnum;
import com.team8.project2.domain.member.repository.FollowRepository;
import com.team8.project2.domain.member.repository.MemberRepository;
import com.team8.project2.global.Rq;
import com.team8.project2.global.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock private MemberRepository memberRepository;
    @Mock private AuthTokenService authTokenService;
    @Mock private FollowRepository followRepository;
    @Mock private Rq rq;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private CurationRepository curationRepository;
    @Mock
    private S3Uploader s3Uploader;

    @Test
    @DisplayName("회원 가입 - null 가능 필드가 모두 채워졌을 때 정상 저장된다.")
    void join_Succeeds_WhenAllNullableFieldsAreFilled() {
        Member member = Member.builder()
                .memberId("fullUser")
                .password("fullpw")
                .email("full@test.com")
                .profileImage("fullImage.png")
                .username("Full Name")
                .introduce("Hello, I'm full user!")
                .role(RoleEnum.MEMBER)
                .build();

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member result = memberService.join(
                "fullUser", "fullpw", RoleEnum.MEMBER, "full@test.com", "fullImage.png"
        );

        assertNotNull(result);
        assertEquals("fullUser", result.getMemberId());
        assertEquals("Full Name", result.getUsername());
        assertEquals("full@test.com", result.getEmail());
        assertEquals("fullImage.png", result.getProfileImage());
    }

    @Test
    @DisplayName("회원 가입 - null 가능 필드가 모두 비어 있을 때 정상 저장된다.")
    void join_Succeeds_WhenAllNullableFieldsAreNull() {
        Member member = Member.builder()
                .memberId("minimalUser")
                .password("minpw")
                .role(RoleEnum.MEMBER)
                .build();

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member result = memberService.join("minimalUser", "minpw", null, null, null);

        assertNotNull(result);
        assertEquals("minimalUser", result.getMemberId());
        assertNull(result.getUsername());
        assertNull(result.getEmail());
        assertNull(result.getProfileImage());
    }

    @Test
    @DisplayName("JWT 발급 - 회원 정보를 이용해 accessToken을 생성한다.")
    void getAuthToken_JWT_토큰을_반환한다() {
        Member member = Member.builder().id(1L).memberId("user").build();
        when(authTokenService.genAccessToken(member)).thenReturn("mocked.jwt.token");

        String token = memberService.getAuthToken(member);

        assertEquals("mocked.jwt.token", token);
    }

    @Test
    @DisplayName("JWT 인증 - accessToken의 payload로부터 사용자 정보를 생성한다.")
    void getMemberByAccessToken_페이로드로부터_Member객체를_생성한다() {
        Map<String, Object> payload = Map.of("id", 1L, "memberId", "user");
        when(authTokenService.getPayload("token123")).thenReturn(payload);

        Optional<Member> result = memberService.getMemberByAccessToken("token123");

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("user", result.get().getMemberId());
    }

    @Test
    @DisplayName("팔로우 - 정상적으로 팔로우 관계가 저장된다.")
    void followUser_정상적으로_팔로우된다() {
        Member follower = Member.builder().id(1L).memberId("user1").build();
        Member followee = Member.builder().id(2L).memberId("user2").build();

        when(memberRepository.findByUsername("user2")).thenReturn(Optional.of(followee));
        when(followRepository.findByFollowerAndFollowee(follower, followee)).thenReturn(Optional.empty());
        when(followRepository.save(any())).thenReturn(new com.team8.project2.domain.member.entity.Follow());

        FollowResDto result = memberService.followUser(follower, "user2");

        assertNotNull(result);
        verify(followRepository).save(any());
    }

    @Test
    @DisplayName("팔로우 - 자기 자신은 팔로우할 수 없다.")
    void followUser_자기자신은_팔로우_불가() {
        Member follower = Member.builder().id(1L).memberId("user1").build();
        Member followee = Member.builder().id(2L).memberId("user1").build(); // 동일 ID

        when(memberRepository.findByUsername("user1")).thenReturn(Optional.of(followee));

        ServiceException ex = assertThrows(ServiceException.class, () ->
                memberService.followUser(follower, "user1"));

        assertEquals("400-1", ex.getCode());
    }
}
