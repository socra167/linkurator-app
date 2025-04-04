package com.team8.project2.domain.playlist.repository;

import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.domain.playlist.entity.PlaylistLike;
import com.team8.project2.domain.playlist.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaylistLikeRepository extends JpaRepository<PlaylistLike, PlaylistLike.PlaylistLikeId> {

    /** 특정 플레이리스트의 좋아요 확인 */
    boolean existsById_PlaylistId(Long playlistId);

    /** 특정 플레이리스트의 좋아요 삭제 */
    void deleteById_PlaylistId(Long playlistId);

    /** 특정 사용자의 모든 좋아요 데이터 조회 */
    List<PlaylistLike> findByIdMemberId(Long memberId);

    /** 특정 플레이리스트의 모든 좋아요 데이터 조회 */
    List<PlaylistLike> findAllById_PlaylistId(Long playlistId);
}
