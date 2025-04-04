package com.team8.project2.domain.link.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 링크 응답 DTO 클래스입니다.
 * 클라이언트에 전달할 링크 데이터를 변환하여 제공합니다.
 */

@Getter
@Setter
public class LinkResDTO {

    /**
     * 링크 URL
     * 링크 TITLE
     * 링크 DESCRIPTION
     * 링크 IMAGE
     */


    private String url;
    private String title;
    private String description;
    private String image;
    private int click;
    private Long linkId;
}
