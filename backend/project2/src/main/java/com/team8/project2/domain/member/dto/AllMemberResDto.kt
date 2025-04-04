package com.team8.project2.domain.member.dto

import com.team8.project2.domain.member.entity.Member

class AllMemberResDto {
    var members: List<MemberResDTO> = listOf()
    var totalPages: Int = 0
    var totalElements: Long = 0
    var numberOfElements: Int = 0
    var size: Int = 0

    companion object {
        @JvmStatic
        fun of(
            members: List<Member?>,
            totalPages: Int,
            totalElements: Long,
            numberOfElements: Int,
            size: Int
        ): AllMemberResDto {
            val dto = AllMemberResDto()
            dto.members = members
                .mapNotNull { it?.let { member -> MemberResDTO.fromEntity(member) } }
            dto.totalPages = totalPages
            dto.totalElements = totalElements
            dto.numberOfElements = numberOfElements
            dto.size = size
            return dto
        }
    }
}