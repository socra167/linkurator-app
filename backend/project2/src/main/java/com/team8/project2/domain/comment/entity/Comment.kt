package com.team8.project2.domain.comment.entity

import com.team8.project2.domain.curation.curation.entity.Curation
import com.team8.project2.domain.member.entity.Member
import jakarta.persistence.*
import lombok.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * 댓글(Comment) 엔티티 클래스입니다.
 * 각 댓글은 특정 큐레이션(Curation)과 연관되며, 작성자 ID 및 내용 정보를 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(
    AuditingEntityListener::class
)
class Comment {
    /**
     * 댓글의 고유 ID (자동 생성)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null

    /**
     * 댓글 작성자의 사용자 ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private var author: Member? = null

    /**
     * 댓글이 속한 큐레이션
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curation_id", nullable = false)
    private var curation: Curation? = null

    @OneToMany(mappedBy = "comment", orphanRemoval = true)
    private val replyComments: MutableList<ReplyComment> = ArrayList()

    /**
     * 댓글 내용 (텍스트 형태, 필수값)
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private var content: String? = null

    /**
     * 댓글 생성 시간 (수정 불가)
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private var createdAt: LocalDateTime? = null

    /**
     * 댓글 수정 시간
     */
    @LastModifiedDate
    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private var modifiedAt: LocalDateTime? = null

    fun addReplyComment(replyComment: ReplyComment) {
        replyComments.add(replyComment)
    }

    val authorName: String
        get() = author!!.username

    val authorId: Long
        get() = author!!.id

    val authorImgUrl: String
        get() = author!!.profileImage

    fun updateContent(content: String?) {
        this.content = content
    }
}

