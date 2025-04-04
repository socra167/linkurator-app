package com.team8.project2.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.domain.member.entity.RoleEnum;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Builder
public class MemberResDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("memberId")
    private String memberId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    private RoleEnum role;

    @JsonProperty("profileImage")
    private String profileImage;

    @JsonProperty("introduce")
    private String introduce;

    @JsonProperty("createdDatetime")
    private LocalDateTime createdDate;

    @JsonProperty("modifiedDatetime")
    private LocalDateTime modifiedDate;

    @JsonProperty("apiKey")
    private String apiKey;

    public static MemberResDTO fromEntity(Member member) {
        return MemberResDTO.builder()
                .id(member.getId())
                .memberId(member.getMemberId())
                .username(member.getUsername())
                .email(member.getEmail())
                .role(member.getRole())
                .profileImage(member.getProfileImage())
                .introduce(member.getIntroduce())
                .createdDate(member.getCreatedDate())
                .modifiedDate(member.getModifiedDate())
                .build();
    }
}
