package com.team8.project2.domain.member.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.team8.project2.domain.member.entity.Member;

import lombok.Getter;

@Getter
public class AllMemberResDto {
	private List<MemberResDTO> members;
	private int totalPages;
	private long totalElements;
	private int numberOfElements;
	private int size;

	public static AllMemberResDto of(List<Member> members, int totalPages, long totalElements, int numberOfElements, int size) {
		AllMemberResDto dto = new AllMemberResDto();
		dto.members = members.stream()
			.map(MemberResDTO::fromEntity)
			.collect(Collectors.toList());
		dto.totalPages = totalPages;
		dto.totalElements = totalElements;
		dto.numberOfElements = numberOfElements;
		dto.size = size;
		return dto;
	}
}
