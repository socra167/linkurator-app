package com.team8.project2.domain.curation.like.entity;

import com.team8.project2.domain.curation.curation.entity.Curation;
import com.team8.project2.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 큐레이션 좋아요(Like) 엔티티 클래스입니다.
 * 특정 회원(Member)이 특정 큐레이션(Curation)에 대해 좋아요를 표시할 수 있습니다.
 */
@Entity
@Getter
@Setter
@Table(name = "likes")
public class Like {

    /**
     * 좋아요의 복합 키를 정의하는 ID 클래스
     */
    @EmbeddedId
    private LikeId id;

    /**
     * 좋아요가 설정된 큐레이션 (다대일 관계)
     */
    @ManyToOne
    @JoinColumn(name = "curationId", insertable=false, updatable=false)
    private Curation curation;

    /**
     * 좋아요를 누른 회원 (다대일 관계)
     */
    @ManyToOne
    @JoinColumn(name = "memberId", insertable=false, updatable=false)
    private Member member;

    /**
     * 좋아요 엔티티의 복합 키 클래스
     */
    @EqualsAndHashCode
    @Embeddable
    @Getter
    @Setter
    public static class LikeId implements Serializable {

        /** 좋아요가 설정된 큐레이션 ID */
        @Column(name = "curationId")
        private Long curationId;

        /** 좋아요를 누른 회원 ID */
        @Column(name = "memberId")
        private Long memberId;

        public LikeId() {}
    }

    /**
     * 좋아요를 설정하는 메서드
     * @param curation 좋아요가 설정될 큐레이션
     * @param member 좋아요를 누른 회원
     * @return 설정된 Like 객체
     */
    public static Like of(Curation curation, Member member) {
        LikeId likeId = new LikeId();
        likeId.setCurationId(curation.getId());
        likeId.setMemberId(member.getId());
        Like like = new Like();
        like.id = likeId;
        like.curation = curation;
        like.member = member;
        return like;
    }
}