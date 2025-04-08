package com.team8.project2.domain.playlist.service

import com.team8.project2.domain.link.service.LinkService
import com.team8.project2.domain.member.repository.MemberRepository
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



}