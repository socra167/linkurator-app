package com.team8.project2.domain.playlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 플레이리스트 생성 요청을 위한 DTO 클래스입니다.
 * 클라이언트가 전송하는 플레이리스트 데이터를 검증하고 전달합니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistCreateDto {

    /**
     * 플레이리스트 제목 (필수)
     */
    @NotBlank(message = "플레이리스트 제목은 필수 입력 사항입니다.")
    private String title;

    /**
     * 플레이리스트 설명 (필수)
     */
    @NotBlank(message = "플레이리스트 설명은 필수 입력 사항입니다.")
    private String description;

    /**
     * 플레이리스트 공개 여부 (기본값: true)
     */
    @NotNull(message = "플레이리스트 공개 여부는 필수 입력 사항입니다.")
    private Boolean isPublic = true;

    /**
     * isPublic이 null인 경우 기본값(true)을 반환합니다.
     * @return 플레이리스트 공개 여부
     */
    public Boolean getIsPublic() {
        return isPublic != null ? isPublic : true;
    }

}

