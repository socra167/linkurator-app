package com.team8.project2.domain.playlist.service

import com.team8.project2.domain.link.service.LinkService
import com.team8.project2.domain.member.entity.Member
import com.team8.project2.domain.member.repository.MemberRepository
import com.team8.project2.domain.playlist.dto.PlaylistCreateDto
import com.team8.project2.domain.playlist.dto.PlaylistDto
import com.team8.project2.domain.playlist.dto.PlaylistUpdateDto
import com.team8.project2.domain.playlist.entity.Playlist
import com.team8.project2.domain.playlist.entity.PlaylistLike
import com.team8.project2.domain.playlist.repository.PlaylistLikeRepository
import com.team8.project2.domain.playlist.repository.PlaylistRepository
import com.team8.project2.global.Rq
import com.team8.project2.global.exception.BadRequestException
import com.team8.project2.global.exception.NotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.InetAddress
import java.net.UnknownHostException
import java.time.Duration

/**
 * 플레이리스트(Playlist) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 플레이리스트 생성, 조회, 수정, 삭제 등의 로직을 처리합니다.
 */
@Service
@Transactional
class PlaylistService(
    private val playlistRepository: PlaylistRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val memberRepository: MemberRepository,
    private val playlistLikeRepository: PlaylistLikeRepository,
    private val rq: Rq,
    private val linkService: LinkService

) {
    companion object {
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

    /**
     * 특정 플레이리스트를 조회합니다.
     * IP 기준으로 중복 조회를 방지하고, Redis에 조회수를 기록합니다.
     *
     * @param id 조회할 플레이리스트 ID
     * @param request 클라이언트 요청 (IP 확인용)
     * @return 조회된 플레이리스트 DTO
     */
    fun getPlaylist(id: Long, request: HttpServletRequest): PlaylistDto {
        val playlist = playlistRepository.findById(id)
            .orElseThrow { NotFoundException("해당 플레이리스트를 찾을 수 없습니다.") }

        val currentViewCount = redisTemplate.opsForZSet()
            .score(VIEW_COUNT_KEY, id.toString())
            ?.toLong() ?: 0L
        playlist.viewCount = currentViewCount

        if (playlist.items == null) {
            playlist.items = mutableListOf()
        }

        val ip = getClientIp(request)
        val redisKey = "playlist_view_${id}_$ip"
        val isNewView = redisTemplate.opsForValue()
            .setIfAbsent(redisKey, "true", Duration.ofDays(1))

        if (isNewView == true) {
            redisTemplate.opsForZSet().incrementScore(VIEW_COUNT_KEY, id.toString(), 1.0)
            playlist.viewCount = currentViewCount + 1
        }

        val actor = if (rq.isLogin) rq.actor else null
        return PlaylistDto.fromEntity(playlist, actor)
    }

    /**
     * 클라이언트의 IP 주소를 추출합니다.
     * 정확한 IP를 얻기 위해 여러 헤더를 순차적으로 검사합니다.
     *
     * @param request 클라이언트의 HTTP 요청
     * @return 추출된 IP 주소 문자열
     */
    private fun getClientIp(request: HttpServletRequest): String? {
        val headers = listOf(
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR",
            "X-Real-IP",
            "X-RealIP",
            "REMOTE_ADDR"
        )

        for (header in headers) {
            val ip = request.getHeader(header)
            if (!ip.isNullOrEmpty() && !ip.equals("unknown", ignoreCase = true)) {
                return ip
            }
        }

        val ip = request.remoteAddr

        return if (ip == "0:0:0:0:0:0:0:1" || ip == "127.0.0.1") {
            try {
                val address = InetAddress.getLocalHost()
                "${address.hostName}/${address.hostAddress}"
            } catch (e: UnknownHostException) {
                throw RuntimeException(e)
            }
        } else {
            ip
        }
    }

    /**
     * Redis에 저장된 플레이리스트 조회수를 DB에 동기화합니다.
     * Redis 키에서 ID를 추출하고, 해당 ID의 조회수를 DB에 반영합니다.
     */
    @Scheduled(fixedRate = 600000)
    fun syncViewCountsToDB() {
        val keys = redisTemplate.keys("$VIEW_COUNT_KEY*")

        keys?.forEach { key ->
            try {
                val idStr = key.removePrefix(VIEW_COUNT_KEY).trim()
                if (idStr.isEmpty()) return@forEach

                val id = idStr.toLong()
                val redisViewCount = redisTemplate.opsForZSet()
                    .score(VIEW_COUNT_KEY, id.toString()) ?: 0.0

                val playlist = playlistRepository.findById(id).orElse(null)
                if (playlist != null) {
                    playlist.viewCount = redisViewCount.toLong()
                    playlistRepository.save(playlist)
                } else {
                    println("Playlist not found in DB for ID: $id")
                }

            } catch (e: NumberFormatException) {
                println("잘못된 Redis 키 형식: $key")
            }
        }
    }


    /**
     * 기존 플레이리스트를 수정합니다.
     *
     * @param id 수정할 플레이리스트 ID
     * @param request 수정할 데이터 DTO
     * @return 수정된 플레이리스트 DTO
     */
    fun updatePlaylist(id: Long, request: PlaylistUpdateDto): PlaylistDto {
        val playlist = playlistRepository.findById(id)
            .orElseThrow { NotFoundException("해당 플레이리스트를 찾을 수 없습니다.") }

        val actor = rq.actor

        request.title?.let { playlist.title = it }
        request.description?.let { playlist.description = it }
        request.isPublic?.let { playlist.isPublic = it }

        return PlaylistDto.fromEntity(playlistRepository.save(playlist), actor)
    }

    /**
     * 플레이리스트를 삭제합니다.
     *
     * @param id 삭제할 플레이리스트 ID
     */
    fun deletePlaylist(id: Long) {
        val playlist = playlistRepository.findById(id)
            .orElseThrow { NotFoundException("해당 플레이리스트를 찾을 수 없습니다.") }

        val actor = rq.actor

        if (playlist.member.getId() != actor.getId()) {
            throw BadRequestException("자신이 소유한 플레이리스트만 삭제할 수 있습니다.")
        }

        if (playlistLikeRepository.existsById_PlaylistId(id)) {
            playlistLikeRepository.deleteById_PlaylistId(id)
        }
        playlistRepository.deleteById(id)
    }


    /**
     * 플레이리스트 좋아요를 토글 처리합니다.
     * Redis로 좋아요 토글, 좋아요 수 업데이트 후 DB에 반영합니다.
     *
     * @param playlistId 대상 플레이리스트 ID
     * @param memberId 좋아요 한 사용자 ID
     */
    @Transactional
    fun likePlaylist(playlistId: Long, memberId: Long) {
        val redisKey = "playlist_like:$playlistId"
        val memberLikedKey = "member_liked_playlists:$memberId"
        val memberStr = memberId.toString()

        val luaScript = """
            if redis.call('SISMEMBER', KEYS[1], ARGV[1]) == 1 then
                redis.call('SREM', KEYS[1], ARGV[1]); return 0;
            else 
                redis.call('SADD', KEYS[1], ARGV[1]); return 1;
            end
        """.trimIndent()

        val result: Long?  = redisTemplate.execute(
            DefaultRedisScript(luaScript, Long::class.java),
            listOf(redisKey),
            memberStr
        )

        if (result != null && result == 1L) {
            redisTemplate.opsForSet().add(memberLikedKey, playlistId.toString())
        } else if (result != null && result == 0L) {
            redisTemplate.opsForSet().remove(memberLikedKey, playlistId.toString())
        }

        val likeCount = redisTemplate.opsForSet().size(redisKey)?: 0L

        val playlist = playlistRepository.findById(playlistId)
            .orElseThrow { NotFoundException("해당 플레이리스트를 찾을 수 없습니다.") }

        playlist.likeCount = likeCount
        playlistRepository.save(playlist)
    }

    /**
     * 사용자가 특정 플레이리스트에 좋아요를 눌렀는지 여부를 확인합니다.
     *
     * @param playlistId 대상 플레이리스트 ID
     * @param memberId 사용자 ID
     * @return 좋아요 여부
     */
    fun hasLikedPlaylist(playlistId: Long, memberId: Long): Boolean {
        val redisKey = "playlist_like:$playlistId"
        return redisTemplate.opsForSet().isMember(redisKey, memberId.toString()) == true
    }


    /**
     * 특정 플레이리스트의 전체 좋아요 수를 조회합니다.
     *
     * @param playlistId 대상 플레이리스트 ID
     * @return 좋아요 수 (없을 경우 0)
     */
    @Transactional(readOnly = true)
    fun getLikeCount(playlistId: Long): Long {
        val redisKey = "playlist_like:$playlistId"
        val count = redisTemplate.opsForSet().size(redisKey)
        return count ?: 0L
    }

    /**
     * 사용자가 좋아요한 모든 플레이리스트 목록을 조회합니다.
     *
     * @param memberId 사용자 ID
     * @return 좋아요한 플레이리스트 DTO 리스트
     */
    @Transactional(readOnly = true)
    fun getLikedPlaylists(memberId: Long): List<PlaylistDto> {
        val likedEntities = playlistLikeRepository.findByIdMemberId(memberId)
        val actor = rq.actor

        val likedPlaylists = likedEntities.map { it.playlist }

        return likedPlaylists.map { PlaylistDto.fromEntity(it, actor) }
    }


    /**
     * Redis에 저장된 사용자의 좋아요 플레이리스트 목록을 조회합니다.
     *
     * @param memberId 사용자 ID
     * @return 좋아요한 플레이리스트 DTO 리스트 (없으면 빈 리스트)
     */
    @Transactional(readOnly = true)
    fun getLikedPlaylistsFromRedis(memberId: Long): List<PlaylistDto> {
        val memberLikedKey = "member_liked_playlists:$memberId"
        val playlistIdObjs = redisTemplate.opsForSet().members(memberLikedKey)

        if (playlistIdObjs == null || playlistIdObjs.isEmpty()) {
            return emptyList()
        }

        val playlistIds = playlistIdObjs.map { it.toString().toLong() }
        val playlists = playlistRepository.findAllById(playlistIds)
        val actor = rq.actor

        return playlists.map { PlaylistDto.fromEntity(it, actor) }
    }


    /**
     * Redis에 저장된 플레이리스트 좋아요 수를 DB에 동기화합니다.
     *
     * Redis의 좋아요를 기반으로 DB에 PlaylistLike 엔티티로 반영하고,
     * 기존 DB와 비교해 좋아요를 반영합니다.
     */
    @Scheduled(fixedRate = 600000)
    @Transactional
    fun syncPlaylistLikesToDB() {
        val keys = redisTemplate.keys("playlist_like:*") ?: return

        for (key in keys) {
            val playlistId = key.split(":").getOrNull(1)?.toLongOrNull() ?: continue

            val playlist = playlistRepository.findById(playlistId).orElse(null) ?: continue
            val rawMemberIds = redisTemplate.opsForSet().members(key) ?: continue
            val memberIds = rawMemberIds.map { it.toString() }.toSet()

            for (memberStr in memberIds) {
                val memberId = memberStr.toLongOrNull() ?: continue
                val member = memberRepository.findById(memberId).orElse(null) ?: continue

                val likeId = PlaylistLike.PlaylistLikeId().apply {
                    this.playlistId = playlistId
                    this.memberId = memberId
                }

                if (!playlistLikeRepository.existsById(likeId)) {
                    val like = PlaylistLike.createLike(playlist, member)
                    playlistLikeRepository.save(like)
                }
            }

            val likeCount = redisTemplate.opsForSet().size(key)
            playlist.likeCount = likeCount ?: 0L
            playlistRepository.save(playlist)

            val currentLikesInDB = playlistLikeRepository.findAllById_PlaylistId(playlistId)
            val currentMemberIdSet = memberIds.mapNotNull { it.toLongOrNull() }.toSet()

            for (dbLike in currentLikesInDB) {
                if (!currentMemberIdSet.contains(dbLike.member.getId())) {
                    playlistLikeRepository.delete(dbLike)
                }
            }
        }
    }





}