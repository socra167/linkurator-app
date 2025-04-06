package com.team8.project2.domain.member.dto;

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

    public void ToMemberReqDTO(Member member) {
        if (this.email != null) member.setEmail(this.email);
        if (this.username != null) member.setUsername(this.username);
        if (this.profileImage != null) member.setProfileImage(this.profileImage);
        if (this.introduce != null) member.setIntroduce(this.introduce);
    }
}
