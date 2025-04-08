package com.team8.project2.domain.curation.tag.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 태그 생성 요청을 위한 DTO 클래스입니다.
 * 클라이언트가 태그 데이터를 전송할 때 사용됩니다.
 */
@Getter
@Setter
public class TagReqDto {

    /**
     * 태그 이름
     */
    private String name;
}
