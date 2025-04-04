package com.team8.project2.domain.playlist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.domain.member.repository.MemberRepository;
import com.team8.project2.domain.playlist.dto.PlaylistCreateDto;
import com.team8.project2.domain.playlist.dto.PlaylistDto;
import com.team8.project2.domain.playlist.dto.PlaylistItemOrderUpdateDto;
import com.team8.project2.domain.playlist.entity.PlaylistItem;
import com.team8.project2.domain.playlist.service.PlaylistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ApiV1PlaylistControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ApiV1PlaylistController playlistController;

    @Mock
    private PlaylistService playlistService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(playlistController).build(); // 🔹 여기서 초기화!
    }

    @Test
    @DisplayName("플레이리스트를 정상적으로 생성해야 한다.")
    void shouldCreatePlaylistSuccessfully() throws Exception {
        // Given
        PlaylistCreateDto request = new PlaylistCreateDto();
        request.setTitle("New Playlist");
        request.setDescription("Description");

        PlaylistDto response = PlaylistDto.builder()
                .id(1L)
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        when(playlistService.createPlaylist(any(PlaylistCreateDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/playlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("New Playlist"));
    }

    @Test
    @DisplayName("플레이리스트에 링크 추가가 정상적으로 이루어져야 한다.")
    void addLinkToPlaylist() throws Exception {
        Long playlistId = 1L;
        String linkIdStr = "100";
        Map<String, String> request = new HashMap<>();
        request.put("linkId", linkIdStr);

        PlaylistDto sampleDto = PlaylistDto.builder()
                .id(playlistId)
                .title("테스트 플레이리스트")
                .description("테스트 설명")
                .build();

        when(playlistService.addPlaylistItem(eq(playlistId), anyLong(), eq(PlaylistItem.PlaylistItemType.LINK)))
                .thenReturn(sampleDto);

        mockMvc.perform(post("/api/v1/playlists/{id}/items/link", playlistId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(playlistId))
                .andExpect(jsonPath("$.data.title").value("테스트 플레이리스트"));
    }

    @Test
    @DisplayName("플레이리스트에서 아이템이 삭제되어야 한다.")
    void deletePlaylistItem() throws Exception {
        Long playlistId = 1L;
        Long itemId = 100L;

        mockMvc.perform(delete("/api/v1/playlists/{id}/items/{itemId}", playlistId, itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("플레이리스트 아이템이 삭제되었습니다."));

        verify(playlistService, times(1)).deletePlaylistItem(playlistId, itemId);
    }


    @Test
    @DisplayName("플레이리스트에서 아이템 순서가 변경되어야 한다.")
    void updatePlaylistItemOrder() throws Exception {
        PlaylistDto updatedDto = PlaylistDto.builder()
                .id(1L)
                .title("테스트 플레이리스트")
                .description("테스트 설명")
                .build();

        List<PlaylistItemOrderUpdateDto> newOrder = Arrays.asList(
                new PlaylistItemOrderUpdateDto(3L, null),
                new PlaylistItemOrderUpdateDto(1L, null),
                new PlaylistItemOrderUpdateDto(2L, null)
        );

        when(playlistService.updatePlaylistItemOrder(eq(1L), anyList()))
                .thenReturn(updatedDto);

        String jsonContent = "[{\"id\":3},{\"id\":1},{\"id\":2}]";

        mockMvc.perform(patch("/api/v1/playlists/1/items/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("플레이리스트 아이템 순서가 변경되었습니다."))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("테스트 플레이리스트"));
    }


    /**
     * ✅ 좋아요 증가 API 테스트
     */
    @Test
    @DisplayName("좋아요 증가 API가 정상적으로 호출되어야 한다.")
    void shouldIncreaseLikeCount() throws Exception {
        Long playlistId = 1L;

        Member mockMember = new Member();
        mockMember.setUsername("testUser");
        Member savedMember = memberRepository.save(mockMember);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(savedMember, null)
        );

        mockMvc.perform(post("/api/v1/playlists/{id}/like", playlistId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("좋아요가 증가되었습니다."));

        verify(playlistService, times(1)).likePlaylist(playlistId, savedMember.getId());
    }

    /**
     * ✅ 추천 API 테스트
     */
    @Test
    @DisplayName("플레이리스트의 추천 목록을 정렬하여 조회할 수 있다.")
    void getRecommendedPlaylistsWithSorting() throws Exception {
        Long playlistId = 1L;

        // ✅ 정렬 기준 추가
        String sortType1 = "views";
        String sortType2 = "likes";
        String sortType3 = "combined";

        List<PlaylistDto> recommended = List.of(
                PlaylistDto.builder().id(2L).title("추천 플레이리스트1").description("설명1").build(),
                PlaylistDto.builder().id(3L).title("추천 플레이리스트2").description("설명2").build()
        );

        // ✅ 모든 정렬 옵션에 대해 Stubbing 설정
        when(playlistService.recommendPlaylist(playlistId, sortType1)).thenReturn(recommended);
        when(playlistService.recommendPlaylist(playlistId, sortType2)).thenReturn(recommended);
        when(playlistService.recommendPlaylist(playlistId, sortType3)).thenReturn(recommended);

        // ✅ "views" 정렬 기준으로 요청 (컨트롤러에서 `combined`이 기본값이므로, 명시적으로 요청해야 함)
        mockMvc.perform(get("/api/v1/playlists/{id}/recommendation", playlistId)
                        .param("sortType", sortType1) // views
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].title").value("추천 플레이리스트1"))
                .andExpect(jsonPath("$.data[1].title").value("추천 플레이리스트2"));

        // ✅ "likes" 정렬 기준으로 요청
        mockMvc.perform(get("/api/v1/playlists/{id}/recommendation", playlistId)
                        .param("sortType", sortType2) // likes
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].title").value("추천 플레이리스트1"))
                .andExpect(jsonPath("$.data[1].title").value("추천 플레이리스트2"));

        // ✅ "combined" 정렬 기준으로 요청
        mockMvc.perform(get("/api/v1/playlists/{id}/recommendation", playlistId)
                        .param("sortType", sortType3) // combined
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].title").value("추천 플레이리스트1"))
                .andExpect(jsonPath("$.data[1].title").value("추천 플레이리스트2"));

        // ✅ verify를 통해 모든 정렬 옵션에 대한 호출을 검증
        verify(playlistService, times(1)).recommendPlaylist(playlistId, sortType1);
        verify(playlistService, times(1)).recommendPlaylist(playlistId, sortType2);
        verify(playlistService, times(1)).recommendPlaylist(playlistId, sortType3);
    }

    @Test
    @DisplayName("플레이리스트 추천 기능이 정렬 기준에 따라 정상 동작해야 한다.")
    void shouldReturnRecommendedPlaylistsSorted() throws Exception {
        Long playlistId = 1L;
        List<PlaylistDto> recommended = List.of(
                PlaylistDto.builder().id(2L).title("추천 플레이리스트1").description("설명1").build(),
                PlaylistDto.builder().id(3L).title("추천 플레이리스트2").description("설명2").build()
        );

        when(playlistService.recommendPlaylist(playlistId, "likes")).thenReturn(recommended);

        mockMvc.perform(get("/api/v1/playlists/{id}/recommendation", playlistId)
                        .param("sortType", "likes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("추천 플레이리스트 목록을 조회하였습니다."))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(2))
                .andExpect(jsonPath("$.data[0].title").value("추천 플레이리스트1"))
                .andExpect(jsonPath("$.data[1].id").value(3));

        verify(playlistService, times(1)).recommendPlaylist(playlistId, "likes");
    }
}
