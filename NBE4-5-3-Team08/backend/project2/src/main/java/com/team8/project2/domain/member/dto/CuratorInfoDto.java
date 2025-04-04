package com.team8.project2.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CuratorInfoDto {
	private String username;
	private String profileImage;
	private String introduce;
	private long curationCount;
	private boolean isFollowed;
	private boolean isLogin;

	public CuratorInfoDto(String username, String profileImage, String introduce, long curationCount,
		boolean isFollowed,
		boolean isLogin) {
		this.username = username;
		this.profileImage = profileImage;
		this.introduce = introduce;
		this.curationCount = curationCount;
		this.isFollowed = isFollowed;
		this.isLogin = isLogin;
	}
}
