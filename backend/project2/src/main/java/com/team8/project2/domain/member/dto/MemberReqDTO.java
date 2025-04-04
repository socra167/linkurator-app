package com.team8.project2.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.domain.member.entity.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberReqDTO {

    @NotBlank(message = "회원 ID는 필수 입력값입니다.")
    @Size(max = 100, message = "회원 ID는 최대 100자까지 입력 가능합니다.")
    @JsonProperty("memberId")
    private String memberId;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    @JsonProperty("password")
    private String password;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @JsonProperty("email")
    private String email;

    @Size(max = 100, message = "사용자 이름은 최대 100자까지 입력 가능합니다.")
    @JsonProperty("username")
    private String username;

    @JsonProperty("profileImage")
    private String profileImage;

    @JsonProperty("introduce")
    private String introduce;

    @JsonProperty("role")
    @Builder.Default
    private RoleEnum role = RoleEnum.MEMBER;

    @JsonCreator
    public void setRole(String role) {
        if (role == null || role.isBlank()) {
            this.role = RoleEnum.MEMBER; // 기본값 설정
        } else {
            try {
                this.role = RoleEnum.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("지원되지 않는 역할입니다: " + role);
            }
        }
    }

    public Member toEntity() {
        return Member.builder()
                .memberId(this.memberId)
                .password(this.password)
                .email(this.email)
                .username(this.username)
                .profileImage(this.profileImage)
                .introduce(this.introduce)
                .role(this.role != null ? this.role : RoleEnum.MEMBER) // 기본값 설정
                .build();
    }
}
