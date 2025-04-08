package com.team8.project2.domain.link.service

import com.team8.project2.domain.link.dto.LinkReqDTO
import com.team8.project2.domain.link.entity.Link
import com.team8.project2.domain.link.repository.LinkRepository
import com.team8.project2.global.exception.ServiceException
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration
import java.util.*

@ExtendWith(MockitoExtension::class)
class LinkServiceTest {

    @Mock
    lateinit var linkRepository: LinkRepository

    @Mock
    lateinit var redisTemplate: RedisTemplate<String, String>

    @Mock
    lateinit var request: HttpServletRequest

    @Mock
    lateinit var linkClickService: LinkClickService

    @InjectMocks
    lateinit var linkService: LinkService

    private lateinit var link: Link

    @BeforeEach
    fun setUp() {
        link = Link.builder()
            //.id(1L)
            .url("https://example.com")
            .click(0)
            .build()
    }

    @Test
    fun `링크 추가`() {
        val dto = LinkReqDTO("https://example.com", "테스트 제목", "테스트 설명")
        `when`(linkRepository.save(any())).thenReturn(link)

        val created = linkService.addLink(dto)

        Assertions.assertNotNull(created)
        Assertions.assertEquals(dto.url, created.url)
        verify(linkRepository, times(1)).save(any())
    }

    @Test
    fun `링크 수정`() {
        val newUrl = "https://updated-example.com"
        `when`(linkRepository.findById(1L)).thenReturn(Optional.of(link))
        `when`(linkRepository.save(any())).thenReturn(link)

        val updated = linkService.updateLink(1L, newUrl)

        Assertions.assertEquals(newUrl, updated.url)
        verify(linkRepository, times(1)).save(any())
    }

    @Test
    fun `링크 수정 실패 - 링크 없음`() {
        `when`(linkRepository.findById(1L)).thenReturn(Optional.empty())

        val ex = Assertions.assertThrows(ServiceException::class.java) {
            linkService.updateLink(1L, "https://updated.com")
        }

        Assertions.assertEquals("404-1", ex.code)
        Assertions.assertEquals("해당 링크를 찾을 수 없습니다.", ex.message)
    }

    @Test
    fun `링크 삭제`() {
        `when`(linkRepository.findById(1L)).thenReturn(Optional.of(link))
        doNothing().`when`(linkRepository).delete(any())

        linkService.deleteLink(1L)

        verify(linkRepository).delete(any())
    }

    @Test
    fun `링크 삭제 실패 - 링크 없음`() {
        `when`(linkRepository.findById(1L)).thenReturn(Optional.empty())

        val ex = Assertions.assertThrows(ServiceException::class.java) {
            linkService.deleteLink(1L)
        }

        Assertions.assertEquals("404-1", ex.code)
        Assertions.assertEquals("해당 링크를 찾을 수 없습니다.", ex.message)
    }

    @Test
    fun `링크 조회 - 없으면 생성`() {
        val url = "https://example.com"
        `when`(linkRepository.findByUrl(url)).thenReturn(Optional.empty())
        `when`(linkRepository.save(any())).thenReturn(link)

        val found = linkService.getLink(url)

        Assertions.assertEquals(url, found.url)
        verify(linkRepository).save(any())
    }

    @Test
    fun `링크 조회 - 있으면 기존 반환`() {
        val url = "https://example.com"
        `when`(linkRepository.findByUrl(url)).thenReturn(Optional.of(link))

        val found = linkService.getLink(url)

        Assertions.assertEquals(url, found.url)
        verify(linkRepository, never()).save(any())
    }

    @Test
    @DisplayName("링크 클릭수는 한 번만 증가해야 한다")
    fun `클릭수 단 1회만 증가`() {
        val valueOps = mock(ValueOperations::class.java) as ValueOperations<String, String>
        `when`(redisTemplate.opsForValue()).thenReturn(valueOps)
        `when`(request.remoteAddr).thenReturn("192.168.0.1")
        `when`(valueOps.setIfAbsent(anyString(), eq("true"), eq(Duration.ofMinutes(10))))
            .thenReturn(true)
            .thenReturn(false)

        `when`(linkRepository.findById(1L)).thenReturn(Optional.of(link))

        repeat(3) { linkService.getLinkAndIncrementClick(1L, request) }

        verify(linkClickService, times(1)).increaseClickCount(any())
    }

    @Test
    @DisplayName("이미 클릭한 경우 클릭수는 증가하지 않는다")
    fun `이미 클릭한 경우 클릭수 증가 안 함`() {
        val valueOps = mock(ValueOperations::class.java) as ValueOperations<String, String>
        `when`(redisTemplate.opsForValue()).thenReturn(valueOps)
        `when`(request.remoteAddr).thenReturn("192.168.0.1")
        `when`(valueOps.setIfAbsent(anyString(), eq("true"), eq(Duration.ofMinutes(10)))).thenReturn(false)

        `when`(linkRepository.findById(1L)).thenReturn(Optional.of(link))

        val beforeClick = link.click
        linkService.getLinkAndIncrementClick(1L, request)

        Assertions.assertEquals(beforeClick, link.click)
        verify(linkClickService, never()).increaseClickCount(any())
    }

    @Test
    @DisplayName("링크 조회 시 존재하지 않으면 예외 발생")
    fun `링크 조회 실패 - 존재하지 않음`() {
        val valueOps = mock(ValueOperations::class.java) as ValueOperations<String, String>
        `when`(redisTemplate.opsForValue()).thenReturn(valueOps)
        `when`(request.remoteAddr).thenReturn("192.168.0.1")
        `when`(valueOps.setIfAbsent(anyString(), eq("true"), eq(Duration.ofMinutes(10)))).thenReturn(true)

        `when`(linkRepository.findById(1L)).thenReturn(Optional.empty())

        val ex = Assertions.assertThrows(ServiceException::class.java) {
            linkService.getLinkAndIncrementClick(1L, request)
        }

        Assertions.assertEquals("404-1", ex.code)
        Assertions.assertEquals("해당 링크를 찾을 수 없습니다.", ex.message)
    }
}
