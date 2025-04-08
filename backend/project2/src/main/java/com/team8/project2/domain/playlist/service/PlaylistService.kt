package com.team8.project2.domain.playlist.service

import com.team8.project2.domain.link.service.LinkService
import com.team8.project2.domain.member.entity.Member
import com.team8.project2.domain.member.repository.MemberRepository
import com.team8.project2.domain.playlist.dto.PlaylistCreateDto
import com.team8.project2.domain.playlist.dto.PlaylistDto
import com.team8.project2.domain.playlist.entity.Playlist
import com.team8.project2.domain.playlist.repository.PlaylistLikeRepository
import com.team8.project2.domain.playlist.repository.PlaylistRepository
import com.team8.project2.global.Rq
import jakarta.transaction.Transactional
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

/**
 * 플레이리스트(Playlist) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 플레이리스트 생성, 조회, 수정, 삭제 등의 로직을 처리합니다.
 */
@Service
@Transactional
class PlaylistService (
    private val playlistRepository: PlaylistRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val memberRepository: MemberRepository,
    private val playlistLikeRepository: PlaylistLikeRepository,
    private val rq: Rq,
    private val linkService: LinkService

){
    companion object{
        private const val VIEW_COUNT_KEY = "playlist:view_count:"
        private const val LIKE_COUNT_KEY = "playlist:like_count:"
        private const val RECOMMEND_KEY = "playlist:recommend:"

    }


    /**
     * 현재 로그인한 사용자의 플레이리스트 목록을 조회합니다.
     *
     * @return 해당 사용자의 플레이리스트 DTO 리스트. 없으면 빈 리스트 반환.
     */
    fun getAllPlaylists(): List<PlaylistDto> {
        val actor = rq.actor
        val playlists = playlistRepository.findByMember(actor)

        return playlists.map { PlaylistDto.fromEntity(it, actor) }
    }

    /**
     * 공개된 모든 플레이리스트를 조회합니다.
     *
     *  @return 공개 플레이리스트 DTO 리스트
     */
    fun getAllPublicPlaylists(): List<PlaylistDto> {
        val playlists = playlistRepository.findAllByIsPublicTrue()
        val actor = if (rq.isLogin) rq.actor else null

        return playlists.map { PlaylistDto.fromEntity(it, actor) }
    }

    /**
     * 현재 로그인한 사용자의 플레이리스트를 생성합니다.
     *
     * @param request 플레이리스트 생성 요청 데이터
     * @return 생성된 플레이리스트 DTO
     */
    fun createPlaylist(request: PlaylistCreateDto): PlaylistDto {
        val actor = rq.actor

        return createPlaylist(request, actor)
    }

    /**
     * 특정 사용자의 플레이리스트를 생성합니다.
     *
     * @param request 플레이리스트 생성 요청 데이터
     * @param member 플레이리스트를 생성할 사용자
     * @return 생성된 플레이리스트 DTO
     */
    fun createPlaylist(request: PlaylistCreateDto, member: Member): PlaylistDto {
        val playlist = Playlist(
            title = request.title,
            description = request.description,
            isPublic = request.isPublic ?: true,
            member = member
        )
        return PlaylistDto.fromEntity(playlistRepository.save(playlist), member)
    }

}