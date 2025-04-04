package com.team8.project2.domain.member.dto;

import com.team8.project2.domain.member.entity.Follow;

import lombok.Getter;

@Getter
public class UnfollowResDto {
	private String followee;

	public static UnfollowResDto fromEntity(Follow follow) {
		UnfollowResDto unfollowResDto = new UnfollowResDto();
		unfollowResDto.followee = follow.getFolloweeName();
		return unfollowResDto;
	}
}
