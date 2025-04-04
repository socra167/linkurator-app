package com.team8.project2.domain.comment.entity;

import com.team8.project2.domain.curation.curation.entity.Curation;
import com.team8.project2.domain.member.entity.Member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 댓글(Comment) 엔티티 클래스입니다.
 * 각 댓글은 특정 큐레이션(Curation)과 연관되며, 작성자 ID 및 내용 정보를 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    /**
     * 댓글의 고유 ID (자동 생성)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 댓글 작성자의 사용자 ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member author;

    /**
     * 댓글이 속한 큐레이션
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curation_id", nullable = false)
    private Curation curation;

    @OneToMany(mappedBy = "comment", orphanRemoval = true)
    private List<ReplyComment> replyComments = new ArrayList<>();

    /**
     * 댓글 내용 (텍스트 형태, 필수값)
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 댓글 생성 시간 (수정 불가)
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt;

    /**
     * 댓글 수정 시간
     */
    @LastModifiedDate
    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime modifiedAt;

    public void addReplyComment(ReplyComment replyComment) {
        this.replyComments.add(replyComment);
    }

    public String getAuthorName() {
        return author.getUsername();
    }

    public long getAuthorId() {
        return author.getId();
    }

    public String getAuthorImgUrl() {
        return author.getProfileImage();
    }

    public void updateContent(String content) {
        this.content = content;
    }
}

