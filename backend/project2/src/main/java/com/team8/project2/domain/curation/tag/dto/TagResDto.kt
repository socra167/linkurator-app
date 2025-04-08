package com.team8.project2.domain.curation.tag.dto;

import java.util.ArrayList;
import java.util.List;

import com.team8.project2.domain.curation.tag.entity.Tag;

import lombok.Getter;

@Getter
public class TagResDto {
	List<String> tags;

	public static TagResDto of(List<Tag> topTags) {
		TagResDto tagResDto = new TagResDto();
		tagResDto.tags = new ArrayList<>();
		topTags.forEach(tag -> tagResDto.tags.add(tag.getName()));
		return tagResDto;
	}
}
