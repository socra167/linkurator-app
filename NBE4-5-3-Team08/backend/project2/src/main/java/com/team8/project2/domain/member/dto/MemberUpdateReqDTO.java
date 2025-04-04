package com.team8.project2.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team8.project2.domain.member.entity.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateReqDTO {

    @NotBlank(message = "회원 ID는 필수 입력값입니다.")
    @Size(max = 100, message = "회원 ID는 최대 100자까지 입력 가능합니다.")
    @JsonProperty("memberId")
    private String memberId;

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

    public MemberReqDTO toMemberReqDTO(String password, RoleEnum role) {
        return MemberReqDTO.builder()
                .memberId(this.memberId)
                .password(password) // 매개변수로 받은 password 설정
                .email(this.email)
                .username(this.username)
                .profileImage(this.profileImage)
                .introduce(this.introduce)
                .role(role != null ? role : RoleEnum.MEMBER) // 매개변수로 받은 role 설정, 기본값 MEMBER
                .build();
    }
}
