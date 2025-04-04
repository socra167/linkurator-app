package com.team8.project2.domain.member.dto;

import java.util.ArrayList;
import java.util.List;

import com.team8.project2.domain.member.entity.Follow;

import lombok.Getter;

@Getter
public class FollowingResDto {
	List<FollowResDto> following = new ArrayList<>();

	public static FollowingResDto fromEntity(List<Follow> followings) {
		FollowingResDto dto = new FollowingResDto();
		for (Follow follow : followings) {
			dto.following.add(FollowResDto.fromEntity(follow));
		}
		return dto;
	}
}
