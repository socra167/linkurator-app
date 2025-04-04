package com.team8.project2.domain.link.service;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team8.project2.domain.link.dto.LinkClickResDto;
import com.team8.project2.domain.link.dto.LinkReqDTO;
import com.team8.project2.domain.link.entity.Link;
import com.team8.project2.domain.link.repository.LinkRepository;
import com.team8.project2.global.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Optional;

/**
 * 링크(Link) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 링크 추가, 수정, 삭제 및 조회 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final LinkClickService linkClickService;
    private final RedisTemplate<String, String> redisTemplate; // RedisTemplate 추가
    private static final String CLICK_KEY = "link:click:"; // Redis 키 접두사
    /**
     * 특정 링크를 조회하고 클릭수를 증가시킵니다.
     *
     * @param linkId 조회할 링크 ID
     * @param request 클라이언트 요청 객체
     * @return 클릭된 링크 객체
     */
    @Transactional
    public LinkClickResDto getLinkAndIncrementClick(Long linkId, HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-RealIP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if(ip.equals("0:0:0:0:0:0:0:1") || ip.equals("127.0.0.1"))
        {
            InetAddress address = null;
            try {
                address = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            ip = address.getHostName() + "/" + address.getHostAddress();
        }

        String key = CLICK_KEY + linkId + ":" + ip;

        // Redis에 먼저 키 저장 (최초 요청만 true 반환, 10분 유지)
        boolean isNewClick = redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(true), Duration.ofMinutes(10));
        System.out.println("Redis Key Set? " + isNewClick + " | Key: " + key);

        // 링크 조회
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new ServiceException("404-1", "해당 링크를 찾을 수 없습니다."));

        // 새로운 조회일 때만 클릭수 증가
        if (isNewClick) {
            linkClickService.increaseClickCount(link);
            System.out.println("클릭수 증가! 현재 조회수: " + link.getClick());
        } else {
            System.out.println("클릭수 증가 안 함 (이미 조회된 IP)");
        }

        return LinkClickResDto.fromEntity(link);
    }


    /**
     * 새로운 링크를 추가합니다.
     *
     * @param linkReqDTO 링크 추가 요청 데이터 객체
     * @return 생성된 링크 객체
     */
    @Transactional
    public Link addLink(LinkReqDTO linkReqDTO) {
        Link link = Link.builder()
                .title(linkReqDTO.getTitle())
                .url(linkReqDTO.getUrl())
                .description(linkReqDTO.getDescription())
                .click(0)
                .build();
        return linkRepository.save(link);
    }


    /**
     * 기존 링크를 수정합니다.
     *
     * @param linkId 수정할 링크 ID
     * @param url    새로운 링크 URL
     * @return 수정된 링크 객체
     */
    @Transactional
    public Link updateLink(Long linkId, String url) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new ServiceException("404-1", "해당 링크를 찾을 수 없습니다."));
        link.setUrl(url);
        return linkRepository.save(link);
    }

    /**
     * 특정 링크를 삭제합니다.
     *
     * @param linkId 삭제할 링크 ID
     */
    @Transactional
    public void deleteLink(Long linkId) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new ServiceException("404-1", "해당 링크를 찾을 수 없습니다."));
        linkRepository.delete(link);
    }

    /**
     * 링크가 존재하면 기존 링크를 반환하고, 존재하지 않으면 새로 생성하여 반환합니다.
     *
     * @param url 조회할 링크 URL
     * @return 기존 또는 새로 생성된 링크 객체
     */
    @Transactional
    public Link getLink(String url) {
        Optional<Link> opLink = linkRepository.findByUrl(url);
        if (opLink.isPresent()) {
            return opLink.get();
        }
        Link link = Link.builder()
            .url(url)
            .build();
        link.loadMetadata();
        return linkRepository.save(link);
    }


    /**
     * 링크의 제목, URL, 설명을 수정합니다.
     *
     * @param linkId      수정할 링크 ID
     * @param title       새로운 링크 제목
     * @param url         새로운 링크 URL
     * @param description 새로운 링크 설명
     * @return 수정된 링크 객체
     */
    @Transactional
    public Link updateLinkDetails(Long linkId, String title, String url, String description) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new ServiceException("404", "해당 링크를 찾을 수 없습니다."));
        link.setTitle(title);
        link.setUrl(url);
        link.setDescription(description);
        return linkRepository.save(link);
    }

}
