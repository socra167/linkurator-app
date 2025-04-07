package com.team8.project2.domain.playlist.entity;

import com.team8.project2.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 플레이리스트 좋아요(PlaylistLike) 엔티티 클래스입니다.
 * 사용자가 플레이리스트에 좋아요를 누른 정보를 저장합니다.
 * @deprecated Kotlin으로 마이그레이션됨. PlaylistLike.kt
 */

@Deprecated
@Entity
@Getter
@Setter
@Table(name = "playlist_likes")
public class PlaylistLike {

    @EmbeddedId
    private PlaylistLikeId id;

    @ManyToOne
    @JoinColumn(name = "playlistId", insertable = false, updatable = false)
    private Playlist playlist;

    @ManyToOne
    @JoinColumn(name = "memberId", insertable = false, updatable = false)
    private Member member;

    @Embeddable
    @EqualsAndHashCode
    @Getter
    @Setter
    public static class PlaylistLikeId implements Serializable {
        private Long playlistId;
        private Long memberId;
    }

    public static PlaylistLike createLike(Playlist playlist, Member member) {
        PlaylistLike like = new PlaylistLike();
        PlaylistLikeId likeId = new PlaylistLikeId();
        likeId.setPlaylistId(playlist.getId());
        likeId.setMemberId(member.getId());

        like.setId(likeId);
        like.setPlaylist(playlist);
        like.setMember(member);
        return like;
    }

}
