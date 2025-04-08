package com.team8.project2.domain.playlist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 플레이리스트 아이템 수정 요청용 DTO입니다.
 * @deprecated Kotlin으로 마이그레이션됨. PlaylistItemUpdateDto.kt
 */
@Deprecated
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistItemUpdateDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "url은 필수입니다.")
    private String url;

    private String description;
}
