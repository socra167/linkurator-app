package com.team8.project2.domain.link.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team8.project2.domain.link.dto.LinkReqDTO;
import com.team8.project2.domain.link.entity.Link;
import com.team8.project2.domain.link.repository.LinkRepository;
import com.team8.project2.domain.member.entity.Member;
import com.team8.project2.domain.member.repository.MemberRepository;
import com.team8.project2.domain.member.service.AuthTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ApiV1LinkControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private LinkRepository linkRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AuthTokenService authTokenService;

    private Member author;
    private String accessToken;

    @BeforeEach
    void setUp() {
        author = memberRepository.findById(1L).orElseThrow();
        accessToken = authTokenService.genAccessToken(author);
    }

    @Test
    @DisplayName("링크 등록이 성공한다")
    void addLink() throws Exception {
        LinkReqDTO dto = new LinkReqDTO();
        dto.setUrl("https://test.com");

        mockMvc.perform(post("/api/v1/link")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("201-1"))
                .andExpect(jsonPath("$.msg").value("링크가 성공적으로 추가되었습니다."))
                .andExpect(jsonPath("$.data.url").value("https://test.com"));
    }

    @Test
    @DisplayName("링크를 수정할 수 있다")
    void updateLink() throws Exception {
        Link saved = linkRepository.save(Link.builder()
                .url("https://original.com")
                .click(0)
                .build());

        LinkReqDTO updateDto = new LinkReqDTO();
        updateDto.setUrl("https://updated.com");

        mockMvc.perform(put("/api/v1/link/{linkId}", saved.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("링크가 성공적으로 수정되었습니다."))
                .andExpect(jsonPath("$.data.url").value("https://updated.com"));
    }

    @Test
    @DisplayName("링크를 삭제할 수 있다")
    void deleteLink() throws Exception {
        Link link = linkRepository.save(Link.builder()
                .url("https://to-be-deleted.com")
                .click(0)
                .build());

        mockMvc.perform(delete("/api/v1/link/{linkId}", link.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value("204-1"))
                .andExpect(jsonPath("$.msg").value("링크가 성공적으로 삭제되었습니다."));

        assertThat(linkRepository.findById(link.getId())).isEmpty();
    }

}


