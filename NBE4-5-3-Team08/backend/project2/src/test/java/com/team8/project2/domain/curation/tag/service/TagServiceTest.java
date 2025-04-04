package com.team8.project2.domain.curation.tag.service;

import com.team8.project2.domain.curation.tag.entity.Tag;
import com.team8.project2.domain.curation.tag.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = Tag.builder().name("testTag").build();
    }

    @Test
    @DisplayName("태그가 존재하면 존재하는 태그 반환한다.")
    void getTag_WhenTagExists_ShouldReturnExistingTag() {
        // given
        when(tagRepository.findByName("testTag")).thenReturn(Optional.of(tag));

        // when
        Tag result = tagService.getTag("testTag");

        // then
        assertThat(result).isEqualTo(tag);
        verify(tagRepository, times(1)).findByName("testTag");
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    @DisplayName("태그가 존재하지 않으면 태그를 생성한다")
    void getTag_WhenTagDoesNotExist_ShouldCreateAndReturnNewTag() {
        // given
        when(tagRepository.findByName("newTag")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Tag result = tagService.getTag("newTag");

        // then
        assertThat(result.getName()).isEqualTo("newTag");
        verify(tagRepository, times(1)).findByName("newTag");
        verify(tagRepository, times(1)).save(any(Tag.class));
    }
}