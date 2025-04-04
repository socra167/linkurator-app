package com.team8.project2.domain.curation.curation.dto;

import com.team8.project2.domain.curation.tag.dto.TagReqDto;
import com.team8.project2.domain.link.dto.LinkReqDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 큐레이션 생성 및 수정 요청을 위한 DTO 클래스입니다.
 * 사용자가 입력한 큐레이션 데이터를 검증하고 전달합니다.
 */
@Getter
@Setter
public class CurationReqDTO {

    /**
     * 큐레이션 제목 (필수값)
     */
    @NotNull
    private String title;

    /**
     * 큐레이션 내용 (필수값)
     */
    @NotNull
    private String content;

    /**
     * 포함된 링크 목록 (선택적)
     */
    private List<LinkReqDTO> linkReqDtos;

    /**
     * 큐레이션 태그 목록 (선택적)
     */
    private List<TagReqDto> tagReqDtos;
}
