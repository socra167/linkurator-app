package com.team8.project2.domain.playlist.dto;

import lombok.Data;

/**
 * 플레이리스트 수정 요청을 위한 DTO 클래스입니다.
 * 클라이언트가 전송하는 수정 데이터를 검증하고 전달합니다.
 */
@Data
public class PlaylistUpdateDto {

    /**
     * 변경할 플레이리스트 제목
     */
    private String title;

    /**
     * 변경할 플레이리스트 설명
     */
    private String description;

    /**
     * 플레이리스트 공개 여부
     */
    private Boolean isPublic;

    /**
     * 공개 여부 값을 반환합니다. 기본값 설정이 필요한 경우 추가할 수 있습니다.
     * @return 공개 여부 값
     */
    public Boolean getIsPublic() {
        return isPublic;
    }
}
