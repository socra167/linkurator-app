package com.team8.project2.domain.admin.service;

import com.team8.project2.domain.admin.dto.StatsResDto;
import com.team8.project2.domain.curation.curation.repository.CurationRepository;
import com.team8.project2.domain.member.repository.MemberRepository;
import com.team8.project2.domain.playlist.repository.PlaylistRepository;
import com.team8.project2.global.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private CurationRepository curationRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlaylistRepository playlistRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ 큐레이션 삭제 테스트 (성공)
    @Test
    @DisplayName("큐레이션 삭제 - 존재하는 큐레이션이면 정상적으로 삭제된다.")
    void deleteCuration_ShouldDelete_WhenCurationExists() {
        Long curationId = 1L;
        when(curationRepository.existsById(curationId)).thenReturn(true);

        adminService.deleteCuration(curationId);

        verify(curationRepository, times(1)).deleteById(curationId);
    }

    // ❌ 큐레이션 삭제 테스트 (실패 - 존재하지 않는 큐레이션)
    @Test
    @DisplayName("큐레이션 삭제 - 존재하지 않는 큐레이션이면 NotFoundException 발생")
    void deleteCuration_ShouldThrowException_WhenCurationNotFound() {
        Long curationId = 1L;
        when(curationRepository.existsById(curationId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> adminService.deleteCuration(curationId));

        assertEquals("큐레이션을 찾을 수 없습니다.", exception.getMessage());
    }

    // ✅ 멤버 삭제 테스트 (성공)
    @Test
    @DisplayName("멤버 삭제 - 존재하는 멤버이면 정상적으로 삭제된다.")
    void deleteMember_ShouldDelete_WhenMemberExists() {
        Long memberId = 1L;
        when(memberRepository.existsById(memberId)).thenReturn(true);

        adminService.deleteMemberById(memberId);

        verify(memberRepository, times(1)).deleteById(memberId);
    }

    // ❌ 멤버 삭제 테스트 (실패 - 존재하지 않는 멤버)
    @Test
    @DisplayName("멤버 삭제 - 존재하지 않는 멤버이면 NotFoundException 발생")
    void deleteMember_ShouldThrowException_WhenMemberNotFound() {
        Long memberId = 1L;
        when(memberRepository.existsById(memberId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> adminService.deleteMemberById(memberId));

        assertEquals("멤버를 찾을 수 없습니다.", exception.getMessage());
    }

    // ✅ 통계 데이터 조회 테스트
    @Test
    @DisplayName("통계 조회 - 큐레이션 및 플레이리스트의 조회수와 좋아요 수를 올바르게 반환한다.")
    void getCurationAndPlaylistStats_ShouldReturnStatsResDto() {
        when(curationRepository.sumTotalViews()).thenReturn(100L);
        when(curationRepository.sumTotalLikes()).thenReturn(50L);
        when(playlistRepository.sumTotalViews()).thenReturn(200L);
        when(playlistRepository.sumTotalLikes()).thenReturn(80L);

        StatsResDto stats = adminService.getCurationAndPlaylistStats();

        assertEquals(100L, stats.getTotalCurationViews());
        assertEquals(50L, stats.getTotalCurationLikes());
        assertEquals(200L, stats.getTotalPlaylistViews());
        assertEquals(80L, stats.getTotalPlaylistLikes());
    }
}
