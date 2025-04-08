package com.team8.project2.domain.admin.controller

import com.team8.project2.domain.admin.service.AdminService
import com.team8.project2.domain.curation.curation.repository.CurationRepository
import com.team8.project2.domain.member.entity.Member
import com.team8.project2.domain.member.repository.MemberRepository
import com.team8.project2.domain.member.service.AuthTokenService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

@Transactional
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("관리자 API 테스트")
internal class ApiV1AdminControllerTest {
    @Autowired
    private val mockMvc: MockMvc? = null

    @Autowired
    private val adminService: AdminService? = null

    @Autowired
    private val memberRepository: MemberRepository? = null

    @Autowired
    private val authTokenService: AuthTokenService? = null


    @Autowired
    private val curationRepository: CurationRepository? = null

    var memberAccessKey: String? = null
    var member: Member? = null

    @BeforeEach
    fun setUp() {
        member = memberRepository!!.findById(4L).get()
        memberAccessKey = authTokenService!!.genAccessToken(member)
    }


    // ✅ 큐레이션 삭제 API 테스트
    @Test
    @DisplayName("큐레이션 삭제 - 관리자 권한으로 큐레이션을 삭제할 수 있다.")
    @Throws(
        Exception::class
    )
    fun deleteCuration_ShouldReturnSuccessResponse() {
        val savedCuration = curationRepository!!.findById(1L).orElseThrow()

        mockMvc!!.perform(
            MockMvcRequestBuilders.delete("/api/v1/admin/curations/{curationId}", savedCuration.id)
                .header("Authorization", "Bearer $memberAccessKey")
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent())
    }

    // ✅ 멤버 삭제 API 테스트
    @Test
    @DisplayName("멤버 삭제 - 관리자 권한으로 멤버를 삭제할 수 있다.")
    @Throws(Exception::class)
    fun deleteMember_ShouldReturnSuccessResponse() {
        mockMvc!!.perform(MockMvcRequestBuilders.delete("/api/v1/admin/members/1"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("Success"))
    }

    @get:Throws(
        Exception::class
    )
    @get:DisplayName("신고된 큐레이션 조회 - 일정 개수 이상 신고된 큐레이션을 조회할 수 있다.")
    @get:Test
    val reportedCurations_ShouldReturnListOfIds: Unit
        // ✅ 신고된 큐레이션 조회 API 테스트
        get() {
            mockMvc!!.perform(
                MockMvcRequestBuilders.get("/api/v1/admin/reported-curations")
                    .param("minReports", "5")
                    .header("Authorization", "Bearer $memberAccessKey")
            )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("200-1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
        }

    @get:Throws(
        Exception::class
    )
    @get:DisplayName("통계 조회 - 큐레이션 및 플레이리스트의 조회수와 좋아요 수를 확인할 수 있다.")
    @get:Test
    val curationAndPlaylistStats_ShouldReturnStats: Unit
        // ✅ 통계 조회 API 테스트
        get() {
            // 실제 테스트 요청
            mockMvc!!.perform(
                MockMvcRequestBuilders.get("/api/v1/admin/stats")
                    .header("Authorization", "Bearer $memberAccessKey")
            )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("200-1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists()) // data 필드가 존재하는지 확인
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.totalCurationViews").exists()
                ) // totalCurationViews 필드 확인
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.data.totalPlaylistViews").exists()
                ) // 예시: totalPlaylistViews도 확인
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalCurationLikes").exists()) // 예시: totalLikes 확인
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalPlaylistLikes").exists()) // 예시: totalLikes 확인
        }
}
