package com.team8.project2.domain.link.service;

import com.team8.project2.domain.link.dto.LinkReqDTO;
import com.team8.project2.domain.link.entity.Link;
import com.team8.project2.domain.link.repository.LinkRepository;
import com.team8.project2.global.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {
    @Mock
    private LinkRepository linkRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private LinkService linkService;


    private Link link;

    @BeforeEach
    void setUp() {
        link = new Link().builder()
                .id(1L)
                .url("https://example.com")
                .click(0)
                .build();
    }

    // 링크 추가 테스트
    @Test
    void AddLink() {
        // given
        String url = "https://example.com";
        LinkReqDTO linkReqDTO = new LinkReqDTO();
        linkReqDTO.setUrl(url);
        linkReqDTO.setTitle("테스트 제목");
        linkReqDTO.setDescription("테스트 설명");

        when(linkRepository.save(any(Link.class))).thenReturn(link);

        // when
        Link createdLink = linkService.addLink(linkReqDTO);

        // then
        assertNotNull(createdLink);
        assertEquals(url, createdLink.getUrl());
        verify(linkRepository, times(1)).save(any(Link.class));
    }

    // 링크 수정 테스트
    @Test
    void UpdateLink() {
        // given
        Long linkId = 1L;
        String newUrl = "https://updated-example.com";
        when(linkRepository.findById(linkId)).thenReturn(java.util.Optional.of(link));
        when(linkRepository.save(any(Link.class))).thenReturn(link);

        // when
        Link updatedLink = linkService.updateLink(linkId, newUrl);

        // then
        assertNotNull(updatedLink);
        assertEquals(newUrl, updatedLink.getUrl());
        verify(linkRepository, times(1)).save(any(Link.class));
    }

    // 링크 수정 시 링크가 없으면 예외 발생 테스트
    @Test
    void UpdateLinkNotFound() {
        // given
        Long linkId = 1L;
        String newUrl = "https://updated-example.com";
        when(linkRepository.findById(linkId)).thenReturn(java.util.Optional.empty());

        // when & then
        ServiceException exception = assertThrows(ServiceException.class, () -> linkService.updateLink(linkId, newUrl));
        assertEquals("404-1", exception.getCode());
        assertEquals("해당 링크를 찾을 수 없습니다.", exception.getMessage());
    }

    // 링크 삭제 테스트
    @Test
    void DeleteLink() {
        // given
        Long linkId = 1L;
        when(linkRepository.findById(linkId)).thenReturn(Optional.of(link));
        doNothing().when(linkRepository).delete(any(Link.class));

        // when
        linkService.deleteLink(linkId);

        // then
        verify(linkRepository, times(1)).delete(any(Link.class));
    }

    // 링크 삭제 시 링크가 없으면 예외 발생 테스트
    @Test
    void DeleteLinkNotFound() {
        // given
        Long linkId = 1L;
        when(linkRepository.findById(linkId)).thenReturn(java.util.Optional.empty());

        // when & then
        ServiceException exception = assertThrows(ServiceException.class, () -> linkService.deleteLink(linkId));
        assertEquals("404-1", exception.getCode());
        assertEquals("해당 링크를 찾을 수 없습니다.", exception.getMessage());
    }

    // 링크가 존재하지 않으면 생성해서 반환하는 테스트
    @Test
    void GetLink() {
        // given
        String url = "https://example.com";
        when(linkRepository.findByUrl(url)).thenReturn(java.util.Optional.empty());
        when(linkRepository.save(any(Link.class))).thenReturn(link);

        // when
        Link foundLink = linkService.getLink(url);

        // then
        assertNotNull(foundLink);
        assertEquals(url, foundLink.getUrl());
        verify(linkRepository, times(1)).save(any(Link.class));
    }

    // 링크가 존재하면 기존 링크 반환하는 테스트
    @Test
    void GetLinkExisting() {
        // given
        String url = "https://example.com";
        when(linkRepository.findByUrl(url)).thenReturn(java.util.Optional.of(link));

        // when
        Link foundLink = linkService.getLink(url);

        // then
        assertNotNull(foundLink);
        assertEquals(url, foundLink.getUrl());
        verify(linkRepository, times(0)).save(any(Link.class));
    }

    // 링크 클릭수 증가 테스트 (새로운 클릭)
    @Test
    @DisplayName("링크 클릭수는 한 번만 증가해야 한다")
    void GetLinkAndIncrementClick_NewClick() {
        // Given: Redis와 링크 관련 의존성 준비
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(request.getRemoteAddr()).thenReturn("192.168.0.1"); // Mock IP 설정

        // 첫 번째 클릭에서만 true 반환하고, 그 이후에는 false 반환하도록 설정
        when(valueOperations.setIfAbsent(anyString(), eq("true"), eq(Duration.ofMinutes(10))))
                .thenReturn(true)  // 첫 번째 클릭에서는 키가 없으므로 true 반환
                .thenReturn(false); // 두 번째 이후의 클릭에서는 키가 이미 있으므로 false 반환

        // 링크 클릭 로직이 제대로 동작하도록 설정
        when(linkRepository.findById(1L)).thenReturn(Optional.of(link));
        when(linkRepository.save(any(Link.class))).thenReturn(link);

        // 클릭수 초기 상태 저장
        int initialClickCount = link.getClick();

        // When: 링크를 여러 번 클릭한다
        linkService.getLinkAndIncrementClick(1L, request);  // 첫 번째 클릭
        linkService.getLinkAndIncrementClick(1L, request);  // 두 번째 클릭
        linkService.getLinkAndIncrementClick(1L, request);  // 세 번째 클릭

        // Then: 클릭수는 한 번만 증가해야 한다
        assertEquals(initialClickCount + 1, link.getClick()); // 클릭수가 1만 증가해야 한다.
    }

    // 이미 클릭한 사용자에 대해 클릭수 증가하지 않는 테스트
    @Test
    @DisplayName("이미 클릭한 경우 클릭수가 증가하지 않아야 한다")
    void GetLinkAndIncrementClick_AlreadyClicked() {
        // Given: Redis와 링크 관련 의존성 준비
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(request.getRemoteAddr()).thenReturn("192.168.0.1");

        // 이미 클릭한 경우 false 반환하도록 설정
        when(valueOperations.setIfAbsent(anyString(), eq("true"), eq(Duration.ofMinutes(10))))
                .thenReturn(false); // 이미 클릭했으므로 false 반환

        // 링크 클릭 로직이 제대로 동작하도록 설정
        when(linkRepository.findById(1L)).thenReturn(Optional.of(link));

        // 클릭수 초기 상태 저장
        int initialClickCount = link.getClick();

        // When: 링크를 클릭한다
        linkService.getLinkAndIncrementClick(1L, request);  // 클릭 시도

        // Then: 클릭수는 증가하지 않아야 한다
        assertEquals(initialClickCount, link.getClick()); // 클릭수가 증가하지 않아야 한다.
        verify(linkRepository, times(0)).save(any(Link.class)); // 링크 클릭수 증가하지 않음
    }

    // 링크가 존재하지 않으면 예외 발생 테스트
    @Test
    @DisplayName("링크가 존재하지 않으면 예외가 발생해야 한다")
    void GetLinkAndIncrementClick_LinkNotFound() {
        // Given: Redis와 링크 관련 의존성 준비
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(request.getRemoteAddr()).thenReturn("192.168.0.1");

        // 첫 번째 클릭에서만 true 반환하도록 설정
        when(valueOperations.setIfAbsent(anyString(), eq("true"), eq(Duration.ofMinutes(10))))
                .thenReturn(true); // 첫 번째 클릭에서는 키가 없으므로 true 반환

        // 링크 조회 시 존재하지 않는 링크를 반환하도록 설정
        when(linkRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then: 링크가 존재하지 않으면 예외 발생
        ServiceException exception = assertThrows(ServiceException.class, () -> linkService.getLinkAndIncrementClick(1L, request));
        assertEquals("404-1", exception.getCode());
        assertEquals("해당 링크를 찾을 수 없습니다.", exception.getMessage());
    }

}