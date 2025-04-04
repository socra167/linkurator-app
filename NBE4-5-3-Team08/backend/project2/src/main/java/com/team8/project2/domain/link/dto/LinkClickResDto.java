package com.team8.project2.domain.link.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.team8.project2.domain.link.entity.Link;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public class LinkClickResDto {
	private Long id;
	private String url;
	private String title;
	private String description;
	private String metaImageUrl;
	private int click;
	private LocalDateTime createdAt;

	public static LinkClickResDto fromEntity(Link link) {
		LinkClickResDto dto = new LinkClickResDto();
		dto.id = link.getId();
		dto.url = link.getUrl();
		dto.title = link.getTitle();
		dto.description = link.getDescription();
		dto.metaImageUrl = link.getMetaImageUrl();
		dto.click = link.getClick();
		dto.createdAt = link.getCreatedAt();
		return dto;
	}
}
