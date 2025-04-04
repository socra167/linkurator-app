package com.team8.project2.domain.playlist.dto;

import com.team8.project2.domain.playlist.entity.PlaylistItem;
import lombok.Builder;
import lombok.Data;

/**
 * 플레이리스트 항목(PlaylistItem) 데이터를 전송하는 DTO 클래스입니다.
 * 엔티티를 DTO로 변환하여 클라이언트에 전달할 데이터를 구성합니다.
 */
@Data
@Builder
public class PlaylistItemDto {

    /**
     * 플레이리스트 항목 ID
     */
    private Long id;

    /**
     * 항목의 고유 ID (예: 음악, 영상 등)
     */
    private Long itemId;

    /**
     * 항목의 유형 (예: 음악, 영상 등)
     */
    private String itemType;

    private String title;
    private String description;
    private String url;

    private Long curationId;
    private Long parentItemId;

    /**
     * 플레이리스트 항목 엔티티를 DTO로 변환합니다.
     * @param playlistItem 변환할 플레이리스트 항목 엔티티
     * @return 변환된 PlaylistItemDto 객체
     */
    public static PlaylistItemDto fromEntity(PlaylistItem playlistItem) {
        return PlaylistItemDto.builder()
                .id(playlistItem.getId())
                .itemId(playlistItem.getItemId())
                .itemType(playlistItem.getItemType().name())
                .title(playlistItem.getLink() != null ? playlistItem.getLink().getTitle() : "")
                .description(playlistItem.getLink() != null ? playlistItem.getLink().getDescription() : "")
                .url(playlistItem.getLink() != null ? playlistItem.getLink().getUrl() : "")
                .curationId(playlistItem.getCuration() != null ? playlistItem.getCuration().getId() : null)
                .parentItemId(playlistItem.getParentItemId())
                .build();
    }
}

