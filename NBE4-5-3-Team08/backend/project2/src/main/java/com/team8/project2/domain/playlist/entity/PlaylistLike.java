package com.team8.project2.domain.playlist.entity;

import com.team8.project2.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

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
