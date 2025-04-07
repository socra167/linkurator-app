package com.team8.project2.domain.playlist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
