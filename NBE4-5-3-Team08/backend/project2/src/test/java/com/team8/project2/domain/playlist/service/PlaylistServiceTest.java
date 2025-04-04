package com.team8.project2.domain.playlist.service;

import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.domain.member.repository.MemberRepository;
import com.team8.project2.domain.playlist.dto.PlaylistCreateDto;
import com.team8.project2.domain.playlist.dto.PlaylistDto;
import com.team8.project2.domain.playlist.dto.PlaylistUpdateDto;
import com.team8.project2.domain.playlist.entity.Playlist;
import com.team8.project2.domain.playlist.entity.PlaylistItem;
import com.team8.project2.domain.playlist.repository.PlaylistLikeRepository;
import com.team8.project2.domain.playlist.repository.PlaylistRepository;
import com.team8.project2.global.exception.BadRequestException;
import com.team8.project2.global.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @InjectMocks
    private PlaylistService playlistService;

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlaylistLikeRepository playlistLikeRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private Playlist samplePlaylist;

    private Member sampleMember;

    @BeforeEach
    void setUp() {
        samplePlaylist = Playlist.builder()
                .id(1L)
                .title("테스트 플레이리스트")
                .tags(new HashSet<>())
                .description("테스트 설명")
                .likeCount(0L)
                .build();

        sampleMember = Member.builder()
                .id(1L)
                .username("테스트 유저")
                .email("test@example.com")
                .build();

        lenient().when(memberRepository.findById(sampleMember.getId())).thenReturn(Optional.of(sampleMember));
        lenient().when(playlistRepository.findById(samplePlaylist.getId())).thenReturn(Optional.of(samplePlaylist));

        lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("플레이리스트를 정상적으로 생성해야 한다.")
    void shouldCreatePlaylistSuccessfully() {
        // Given
        PlaylistCreateDto request = new PlaylistCreateDto();
        request.setTitle("새 플레이리스트");
        request.setDescription("새로운 설명");

        Playlist newPlaylist = Playlist.builder()
                .id(2L)
                .title(request.getTitle())
                .description(request.getDescription())
                .tags(Set.of())
                .build();

        when(playlistRepository.save(any(Playlist.class))).thenReturn(newPlaylist);

        // When
        PlaylistDto createdPlaylist = playlistService.createPlaylist(request);

        // Then
        assertNotNull(createdPlaylist);
        assertEquals(request.getTitle(), createdPlaylist.getTitle());
        assertEquals(request.getDescription(), createdPlaylist.getDescription());
    }

    @Test
    @DisplayName("플레이리스트를 정상적으로 조회해야 한다.")
    void shouldRetrievePlaylistSuccessfully() {
        // Given
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(samplePlaylist));

        // When
        PlaylistDto foundPlaylist = playlistService.getPlaylist(1L);

        // Then
        assertNotNull(foundPlaylist);
        assertEquals(samplePlaylist.getTitle(), foundPlaylist.getTitle());
        assertEquals(samplePlaylist.getDescription(), foundPlaylist.getDescription());
    }

    @Test
    @DisplayName("존재하지 않는 플레이리스트 조회 시 NotFoundException이 발생해야 한다.")
    void shouldThrowNotFoundExceptionWhenPlaylistDoesNotExist() {
        // Given
        when(playlistRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> playlistService.getPlaylist(99L));
    }

    @Test
    @DisplayName("모든 플레이리스트를 정상적으로 조회해야 한다.")
    void shouldRetrieveAllPlaylistsSuccessfully() {
        // Given
        List<Playlist> playlists = Arrays.asList(samplePlaylist);
        when(playlistRepository.findAll()).thenReturn(playlists);

        // When
        List<PlaylistDto> foundPlaylists = playlistService.getAllPlaylists();

        // Then
        assertFalse(foundPlaylists.isEmpty());
        assertEquals(1, foundPlaylists.size());
    }

    @Test
    @DisplayName("플레이리스트를 정상적으로 수정해야 한다.")
    void shouldUpdatePlaylistSuccessfully() {
        // Given
        PlaylistUpdateDto request = new PlaylistUpdateDto();
        request.setTitle("수정된 플레이리스트");
        request.setDescription("수정된 설명");

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(samplePlaylist));
        when(playlistRepository.save(any(Playlist.class))).thenReturn(samplePlaylist);

        // When
        PlaylistDto updatedPlaylist = playlistService.updatePlaylist(1L, request);

        // Then
        assertNotNull(updatedPlaylist);
        assertEquals(request.getTitle(), updatedPlaylist.getTitle());
        assertEquals(request.getDescription(), updatedPlaylist.getDescription());
    }

    @Test
    @DisplayName("플레이리스트를 정상적으로 삭제해야 한다.")
    void shouldDeletePlaylistSuccessfully() {
        // Given
        when(playlistRepository.existsById(1L)).thenReturn(true);
        doNothing().when(playlistRepository).deleteById(1L);

        // When & Then
        assertDoesNotThrow(() -> playlistService.deletePlaylist(1L));
    }

    @Test
    @DisplayName("존재하지 않는 플레이리스트 삭제 시 NotFoundException이 발생해야 한다.")
    void shouldThrowNotFoundExceptionWhenDeletingNonExistingPlaylist() {
        // Given
        when(playlistRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThrows(NotFoundException.class, () -> playlistService.deletePlaylist(99L));
    }

    @Test
    @DisplayName("플레이리스트에 아이템을 추가할 수 있다.")
    void addPlaylistItem() {
        // Given
        Long newItemId = 100L;

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(samplePlaylist));
        when(playlistRepository.save(any(Playlist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PlaylistDto updatedPlaylist = playlistService.addPlaylistItem(1L, newItemId, PlaylistItem.PlaylistItemType.LINK);

        // Then
        assertNotNull(updatedPlaylist);
        assertEquals("테스트 플레이리스트", updatedPlaylist.getTitle());
        assertFalse(updatedPlaylist.getItems().isEmpty());
        assertEquals(newItemId, updatedPlaylist.getItems().get(0).getItemId());
        assertEquals("LINK", updatedPlaylist.getItems().get(0).getItemType());
    }

    @Test
    @DisplayName("실패 - 존재하지 않는 플레이리스트에 아이템을 추가할 수 없다.")
    void addPlaylistItemNotFound() {
        // Given
        Long newItemId = 100L;
        when(playlistRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () ->
                playlistService.addPlaylistItem(1L, newItemId, PlaylistItem.PlaylistItemType.LINK));
    }

    @Test
    @DisplayName("플레이리스트에서 아이템을 삭제할 수 있다.")
    void deletePlaylistItem() {
        // Given
        Long itemIdToDelete = 100L;

        PlaylistItem item1 = PlaylistItem.builder().itemId(100L).itemType(PlaylistItem.PlaylistItemType.LINK).build();
        PlaylistItem item2 = PlaylistItem.builder().itemId(101L).itemType(PlaylistItem.PlaylistItemType.CURATION).build();

        samplePlaylist.setItems(new ArrayList<>(Arrays.asList(item1, item2)));
        when(playlistRepository.findById(samplePlaylist.getId())).thenReturn(Optional.of(samplePlaylist));

        // When
        playlistService.deletePlaylistItem(samplePlaylist.getId(), itemIdToDelete);

        // Then
        assertFalse(samplePlaylist.getItems().stream()
                .anyMatch(item -> item.getItemId().equals(itemIdToDelete)));
        verify(playlistRepository, times(1)).save(samplePlaylist);
    }

    @Test
    @DisplayName("실패 - 존재하지 않는 아이템은 삭제할 수 없다.")
    void deletePlaylistItemNotFound() {
        // Given
        Long itemIdToDelete = 100L;
        samplePlaylist.setItems(new ArrayList<>());

        when(playlistRepository.findById(samplePlaylist.getId()))
                .thenReturn(Optional.of(samplePlaylist));

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            playlistService.deletePlaylistItem(samplePlaylist.getId(), itemIdToDelete);
        });
    }

    @Test
    @DisplayName("플레이리스트 아이템 순서를 변경할 수 있다.")
    void updatePlaylistItemOrder() {
        // Given
        PlaylistItem item1 = PlaylistItem.builder().id(1L).itemId(100L).displayOrder(0).itemType(PlaylistItem.PlaylistItemType.LINK).build();
        PlaylistItem item2 = PlaylistItem.builder().id(2L).itemId(101L).displayOrder(1).itemType(PlaylistItem.PlaylistItemType.CURATION).build();
        PlaylistItem item3 = PlaylistItem.builder().id(3L).itemId(102L).displayOrder(2).itemType(PlaylistItem.PlaylistItemType.LINK).build();
        samplePlaylist.setItems(new ArrayList<>(Arrays.asList(item1, item2, item3)));

        List<Long> newOrder = Arrays.asList(3L, 1L, 2L);

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(samplePlaylist));
        when(playlistRepository.save(any(Playlist.class))).thenReturn(samplePlaylist);

        // When
        PlaylistDto updatedDto = playlistService.updatePlaylistItemOrder(1L, newOrder);

        // Then
        assertEquals(0, samplePlaylist.getItems().stream().filter(item -> item.getId().equals(3L)).findFirst().get().getDisplayOrder());
        assertEquals(1, samplePlaylist.getItems().stream().filter(item -> item.getId().equals(1L)).findFirst().get().getDisplayOrder());
        assertEquals(2, samplePlaylist.getItems().stream().filter(item -> item.getId().equals(2L)).findFirst().get().getDisplayOrder());

        assertNotNull(updatedDto);
        assertEquals("테스트 플레이리스트", updatedDto.getTitle());
    }

    @Test
    @DisplayName("실패 - 플레이리스트 아이템 순서 변경 시 아이템 개수가 일치해야 한다.")
    void updatePlaylistItemOrder_itemCount() {
        // Given
        PlaylistItem item1 = PlaylistItem.builder().id(1L).itemId(100L).displayOrder(0).itemType(PlaylistItem.PlaylistItemType.LINK).build();
        PlaylistItem item2 = PlaylistItem.builder().id(2L).itemId(101L).displayOrder(1).itemType(PlaylistItem.PlaylistItemType.CURATION).build();
        PlaylistItem item3 = PlaylistItem.builder().id(3L).itemId(102L).displayOrder(2).itemType(PlaylistItem.PlaylistItemType.LINK).build();
        samplePlaylist.setItems(new ArrayList<>(Arrays.asList(item1, item2, item3)));

        List<Long> newOrder = Arrays.asList(3L, 1L);

        when(playlistRepository.findById(1L)).thenReturn(Optional.of(samplePlaylist));

        // When & Then
        assertThrows(BadRequestException.class, () ->
                playlistService.updatePlaylistItemOrder(1L, newOrder));
    }

    /** ✅ 조회수 증가 테스트 (Redis 반영) */
    @Test
    @DisplayName("조회수가 Redis에서 정상적으로 증가해야 한다.")
    void shouldIncreaseViewCountInRedis() {
        Long playlistId = 1L;

        // When
        playlistService.recordPlaylistView(playlistId);

        // Then
        verify(zSetOperations, times(1)).incrementScore("playlist:view_count", playlistId.toString(), 1);
    }

    /** ✅ 좋아요 증가 테스트 (Redis 반영) */
    @Test
    @DisplayName("좋아요가 Redis에서 정상적으로 증가해야 한다.")
    void shouldIncreaseLikeCountInRedis() {
        Long playlistId = 1L;
        Long memberId = 1L;

        // Given
        when(zSetOperations.score("playlist:like_count", playlistId.toString()))
                .thenReturn(1.0);
        when(redisTemplate.opsForZSet().incrementScore("playlist:like_count", playlistId.toString(), 1))
                .thenReturn(1.0);

        // When
        playlistService.likePlaylist(playlistId, memberId);

        // Then
        verify(zSetOperations, times(1)).incrementScore("playlist:like_count", playlistId.toString(), 1);
        assertEquals(1L, samplePlaylist.getLikeCount());
    }

    /** ✅ 추천 플레이리스트 조회 테스트 (Redis 캐싱 적용) */
    @Test
    @DisplayName("추천 API가 Redis 캐싱을 사용하여 정상적으로 동작해야 한다.")
    void shouldRetrieveRecommendedPlaylistsFromCache() {
        Long playlistId = 1L;
        List<Long> cachedPlaylistIds = Arrays.asList(2L, 3L);

        // Given - Redis에서 추천 데이터가 존재하는 경우
        when(valueOperations.get("playlist:recommend:" + playlistId)).thenReturn(cachedPlaylistIds);
        when(playlistRepository.findAllById(cachedPlaylistIds))
                .thenReturn(Arrays.asList(
                        Playlist.builder().id(2L).title("추천1").description("설명1").tags(new HashSet<>()).build(),
                        Playlist.builder().id(3L).title("추천2").description("설명2").tags(new HashSet<>()).build()
                ));

        // When
        List<PlaylistDto> recommendations = playlistService.recommendPlaylist(playlistId, "likes");

        // Then
        assertEquals(2, recommendations.size());
        verify(valueOperations, times(1)).get("playlist:recommend:" + playlistId);
        verify(playlistRepository, times(1)).findAllById(cachedPlaylistIds);
    }

    /** ✅ 정렬별 추천 테스트 */
    @Test
    @DisplayName("추천 플레이리스트가 좋아요 순으로 정렬되어야 한다.")
    void shouldSortRecommendedPlaylistsByLikes() {
        // Given
        Long playlistId = 1L;
        String sortType = "likes";

        // ✅ Mock 플레이리스트 생성 (ID, 태그 포함)
        Playlist samplePlaylist = Playlist.builder()
                .id(playlistId)
                .title("테스트 플레이리스트")
                .description("테스트 설명")
                .tags(new HashSet<>())
                .build();

        // ✅ `findById()`가 특정 플레이리스트를 반환하도록 Mock 설정
        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(samplePlaylist));

        // ✅ 추천 리스트 (Mock)
        List<Playlist> mockPlaylists = Arrays.asList(
                Playlist.builder().id(2L).title("추천 플레이리스트1").description("설명1").tags(new HashSet<>()).build(),
                Playlist.builder().id(3L).title("추천 플레이리스트2").description("설명2").tags(new HashSet<>()).build()
        );

        when(playlistRepository.findAllById(any())).thenReturn(mockPlaylists);

        // When
        List<PlaylistDto> result = playlistService.recommendPlaylist(playlistId, sortType);

        // Then
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals("추천 플레이리스트1", result.get(0).getTitle());
        assertEquals(3L, result.get(1).getId());
        assertEquals("추천 플레이리스트2", result.get(1).getTitle());

        // ✅ Mock 메서드 호출 검증
        verify(playlistRepository, times(1)).findById(playlistId);
    }

    @Test
    @DisplayName("추천 플레이리스트가 조회수 순으로 정렬되어야 한다.")
    void shouldSortRecommendedPlaylistsByViews() {
        Long playlistId = 1L;
        String sortType = "views";

        Playlist samplePlaylist = Playlist.builder()
                .id(playlistId)
                .title("테스트 플레이리스트")
                .description("테스트 설명")
                .tags(new HashSet<>())
                .build();

        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(samplePlaylist));

        List<Playlist> mockPlaylists = Arrays.asList(
                Playlist.builder().id(2L).title("추천1").description("설명1").tags(new HashSet<>()).build(),
                Playlist.builder().id(3L).title("추천2").description("설명2").tags(new HashSet<>()).build()
        );

        when(playlistRepository.findAllById(any())).thenReturn(mockPlaylists);

        List<PlaylistDto> recommendations = playlistService.recommendPlaylist(playlistId, sortType);

        // Then
        assertNotNull(recommendations);
        assertEquals(2, recommendations.size());
    }


    @Test
    @DisplayName("추천 플레이리스트가 좋아요+조회수 복합 점수 순으로 정렬되어야 한다.")
    void shouldSortRecommendedPlaylistsByCombined() {
        Long playlistId = 1L;
        String sortType = "combined";

        // ✅ Mock 플레이리스트 추가 (findById가 null 반환하지 않도록 설정)
        Playlist samplePlaylist = Playlist.builder()
                .id(playlistId)
                .title("테스트 플레이리스트")
                .description("테스트 설명")
                .tags(new HashSet<>())
                .build();

        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(samplePlaylist));

        // ✅ 추천 리스트 Mock 설정
        List<Playlist> playlists = Arrays.asList(
                Playlist.builder().id(2L).title("추천1").description("설명1").tags(new HashSet<>()).build(),
                Playlist.builder().id(3L).title("추천2").description("설명2").tags(new HashSet<>()).build()
        );

        when(playlistRepository.findAllById(any())).thenReturn(playlists);

        // ✅ 실행
        List<PlaylistDto> recommendations = playlistService.recommendPlaylist(playlistId, sortType);

        // ✅ 검증
        assertNotNull(recommendations);
        assertEquals(2, recommendations.size());
    }


    @Test
    @DisplayName("Redis 캐싱이 없을 때 추천 알고리즘을 실행해야 한다.")
    void shouldRunRecommendationAlgorithmIfCacheMiss() {
        Long playlistId = 1L;
        String sortType = "combined";

        // ✅ Redis 캐시 없음
        when(valueOperations.get("playlist:recommend:" + playlistId)).thenReturn(null);

        // ✅ 정확한 key 값으로 Stubbing 설정
        Set<Object> trendingPlaylists = new HashSet<>(Arrays.asList("2", "3"));
        Set<Object> popularPlaylists = new HashSet<>(Arrays.asList("3", "4"));

        doReturn(trendingPlaylists).when(zSetOperations).reverseRange(eq("playlist:view_count"), eq(0L), eq(5L));
        doReturn(popularPlaylists).when(zSetOperations).reverseRange(eq("playlist:like_count"), eq(0L), eq(5L));

        doReturn(trendingPlaylists).when(zSetOperations).reverseRange(eq("trending:24h"), eq(0L), eq(5L));
        doReturn(popularPlaylists).when(zSetOperations).reverseRange(eq("popular:24h"), eq(0L), eq(5L));

        // ✅ Mock된 플레이리스트 데이터 준비
        List<Long> recommendedPlaylistIds = Arrays.asList(2L, 3L, 4L);
        List<Playlist> mockPlaylists = Arrays.asList(
                Playlist.builder().id(2L).title("추천1").description("설명1").tags(new HashSet<>()).build(),
                Playlist.builder().id(3L).title("추천2").description("설명2").tags(new HashSet<>()).build(),
                Playlist.builder().id(4L).title("추천3").description("설명3").tags(new HashSet<>()).build()
        );

        when(playlistRepository.findAllById(recommendedPlaylistIds)).thenReturn(mockPlaylists);
        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(samplePlaylist));

        // ✅ 실행
        List<PlaylistDto> recommendations = playlistService.recommendPlaylist(playlistId, sortType);

        // ✅ 검증
        assertEquals(3, recommendations.size());

        verify(valueOperations, times(1)).set(eq("playlist:recommend:" + playlistId), any(), any());
    }




}
