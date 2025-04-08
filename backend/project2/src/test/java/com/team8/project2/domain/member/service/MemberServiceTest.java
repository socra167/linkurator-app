package com.team8.project2.domain.member.service;

import com.team8.project2.domain.member.dto.FollowResDto;
import com.team8.project2.domain.member.entity.Follow;
import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.domain.member.entity.RoleEnum;
import com.team8.project2.domain.member.repository.FollowRepository;
import com.team8.project2.domain.member.repository.MemberRepository;
import com.team8.project2.global.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AuthTokenService authTokenService;
    @Mock
    private FollowRepository followRepository;

    @Test
    @DisplayName("회원 가입 - null 가능 필드가 모두 채워졌을 때 정상 저장된다.")
    void join_Succeeds_WhenAllNullableFieldsAreFilled() {
        Member member = new Member(
                "fullUser",                  // memberId
                "Full Name",                 // username
                "fullpw",                    // password
                RoleEnum.MEMBER,             // roleEnum
                "fullImage.png",             // profileImage
                "full@test.com",             // email
                "Hello, I'm full user!"      // introduce
        );

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
        Member member = new Member(
                "minimalUser",     // memberId
                "minpw",           // password
                RoleEnum.MEMBER,   // roleEnum
                null,              // email
                null,              // profileImage
                null               // introduce
        );

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member result = memberService.join("minimalUser", "minpw", null, null, null);

        assertNotNull(result);
        assertEquals("minimalUser", result.getMemberId());
        assertEquals("Anonymous", result.getUsername());
        assertNull(result.getEmail());
        assertNull(result.getProfileImage());
    }

    @Test
    @DisplayName("회원 삭제 - 존재하는 회원이면 정상적으로 삭제된다.")
    void deleteMember_Succeeds_WhenMemberExists() {
        // given
        Member member = new Member("user1");

        when(memberRepository.findByMemberId("user1")).thenReturn(Optional.of(member));

        // when
        memberService.deleteMember("user1");

        // then
        verify(memberRepository).delete(member);
    }

    @Test
    @DisplayName("JWT 발급 - 회원 정보를 이용해 accessToken을 생성한다.")
    void getAuthToken_ReturnsAccessToken_WhenMemberIsValid() {
        Member member = new Member(1L, "user");
        when(authTokenService.genAccessToken(member)).thenReturn("mocked.jwt.token");

        String token = memberService.getAuthToken(member);

        assertEquals("mocked.jwt.token", token);
    }

    @Test
    @DisplayName("JWT 인증 - accessToken의 payload로부터 사용자 정보를 생성한다.")
    void getMemberByAccessToken_ReturnsMember_WhenPayloadIsValid() {
        Map<String, Object> payload = Map.of("id", 1L, "memberId", "user");
        when(authTokenService.getPayload("token123")).thenReturn(payload);

        Optional<Member> result = memberService.getMemberByAccessToken("token123");

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("user", result.get().getMemberId());
    }

    @Test
    @DisplayName("팔로우 - 정상적으로 팔로우 관계가 저장된다.")
    void followUser_Succeeds_WhenValidFolloweeIsGiven() {
        Member follower = new Member(1L, "user1");
        Member followee = new Member(2L, "user2");


        when(memberRepository.findByUsername("user2")).thenReturn(Optional.of(followee));
        when(followRepository.findByFollowerAndFollowee(follower, followee)).thenReturn(Optional.empty());
        Follow follow = new Follow();
        follow.setFollowerAndFollowee(follower, followee);
        when(followRepository.save(any())).thenReturn(follow);
        FollowResDto result = memberService.followUser(follower, "user2");

        assertNotNull(result);
        verify(followRepository).save(any());
    }

    @Test
    @DisplayName("팔로우 - 자기 자신은 팔로우할 수 없다.")
    void followUser_Fails_WhenFollowSelf() {
        Member follower = new Member(1L, "user1");

        // followee도 동일한 user1로 세팅
        when(memberRepository.findByUsername("user1")).thenReturn(Optional.of(follower));

        ServiceException ex = assertThrows(ServiceException.class, () ->
                memberService.followUser(follower, "user1"));

        assertEquals("400-1", ex.getCode());
    }
}
