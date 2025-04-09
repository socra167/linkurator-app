package com.team8.project2.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.domain.member.entity.RoleEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
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

    public MemberResDTO(
            long id,
            String memberId,
            String username,
            String email,
            RoleEnum role,
            String profileImage,
            String introduce,
            LocalDateTime createdDate,
            LocalDateTime modifiedDate
    ) {
        this.id = id;
        this.memberId = memberId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.profileImage = profileImage;
        this.introduce = introduce;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }


    public static MemberResDTO fromEntity(Member member) {
        return new MemberResDTO(
                member.getId(),
                member.getMemberId(),
                member.getUsername(),
                member.getEmail(),
                member.getRole(),
                member.getProfileImage(),
                member.getIntroduce(),
                member.getCreatedDate(),
                member.getModifiedDate()
        );
    }
}
