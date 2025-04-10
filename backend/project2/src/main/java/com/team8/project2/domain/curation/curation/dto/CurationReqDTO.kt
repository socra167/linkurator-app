package com.team8.project2.domain.curation.curation.dto

import com.team8.project2.domain.curation.tag.dto.TagReqDto
import com.team8.project2.domain.link.dto.LinkReqDTO
import jakarta.validation.constraints.NotNull

/**
 * 큐레이션 생성 및 수정 요청을 위한 DTO 클래스입니다.
 * 사용자가 입력한 큐레이션 데이터를 검증하고 전달합니다.
 */
data class CurationReqDTO(
    @field:NotNull
    val title: String,

    @field:NotNull
    val content: String,

    val linkReqDtos: List<LinkReqDTO>? = null,

    val tagReqDtos: List<TagReqDto>? = null


) {
    companion object {
        fun from(
            title: String,
            content: String,
            linkReqDtos: List<LinkReqDTO>? = null,
            tagReqDtos: List<TagReqDto>? = null
        ): CurationReqDTO {
            return CurationReqDTO(
                title = title,
                content = content,
                linkReqDtos = linkReqDtos,
                tagReqDtos = tagReqDtos
            )
        }
    }
}
