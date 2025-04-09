package com.team8.project2.global.security;

import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.domain.member.service.MemberService;
import com.team8.project2.global.Rq;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final Rq rq;
    private final MemberService memberService;

    private boolean isAuthorizationHeaderPresent() {
        String authorizationHeader = rq.getHeader("Authorization");
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
    }

    private String extractAccessToken() {
        if (isAuthorizationHeaderPresent()) {
            String authorizationHeader = rq.getHeader("Authorization");
            return authorizationHeader.substring("Bearer ".length()).trim();
        }
        return rq.getValueFromCookie("accessToken");
    }

    private Member authenticateMember(String accessToken) {
        Optional<Member> opMember = memberService.getMemberByAccessToken(accessToken);
        return opMember.orElse(null);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 로그인, 회원가입, 로그아웃 요청은 필터를 적용하지 않음
        if (List.of("/api/v1/members/login", "/api/v1/members/join", "/api/v1/members/logout").contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = extractAccessToken();
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Member authenticatedMember = authenticateMember(accessToken);
        if (authenticatedMember == null) {
            filterChain.doFilter(request, response);
            return;
        }

        rq.setLogin(authenticatedMember);
        SecurityContextHolder.getContext().setAuthentication(rq.getAuthentication(authenticatedMember));

        filterChain.doFilter(request, response);
    }
}