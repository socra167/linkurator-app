package com.team8.project2.domain.comment.dto

import com.team8.project2.domain.comment.entity.ReplyComment
import java.time.LocalDateTime

/**
 * 답글(ReplyComment) 데이터 전송 객체(DTO) 클래스입니다.
 * 엔티티와의 변환을 지원하며, 클라이언트와의 데이터 교환을 담당합니다.
 */
data class ReplyCommentDto(

    /** 댓글 ID */
    val id: Long?,

    /** 답글 작성자의 id */
    val authorId: Long,

    /** 댓글 작성자의 사용자명 */
    val authorName: String,

    /** 답글 작성자의 프로필 이미지 */
    val authorProfileImageUrl: String,

    /** 댓글 내용 */
    val content: String,

    /** 댓글 생성 시간 */
    val createdAt: LocalDateTime?,

    /** 댓글 수정 시간 */
    val modifiedAt: LocalDateTime?

) {
    companion object {
        fun fromEntity(reply: ReplyComment): ReplyCommentDto {
            return ReplyCommentDto(
                id = reply.id,
                authorId = reply.author.id,
                authorName = reply.author.username,
                authorProfileImageUrl = reply.author.profileImage,
                content = reply.content,
                createdAt = reply.createdAt,
                modifiedAt = reply.modifiedAt
            )
        }
    }
}
