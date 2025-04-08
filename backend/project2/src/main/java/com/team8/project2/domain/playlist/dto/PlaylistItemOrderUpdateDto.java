package com.team8.project2.domain.playlist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 플레이리스트 아이템 순서 변경 요청용 DTO입니다.
 * 메인 아이템 ID와 큐레이션 그룹일 경우, 하위 아이템 ID 목록을 포함합니다.
 * @deprecated Kotlin으로 마이그레이션됨. PlaylistItemOrderUpdateDto.kt
 */
@Deprecated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistItemOrderUpdateDto {
    // 메인 아이템 또는 그룹 헤더의 ID
    private Long id;

    // 만약 해당 아이템이 큐레이션 그룹 헤더라면, 그룹 내 아이템의 순서를 나타내는 ID 목록
    private List<Long> children;
}
