package tobyspring.splearn.adapter.webapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.assertj.MockMvcTester
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.transaction.annotation.Transactional
import tobyspring.splearn.adapter.webapi.dto.MemberRegisterResponse
import tobyspring.splearn.application.member.provided.MemberRegister
import tobyspring.splearn.application.member.required.MemberRepository
import tobyspring.splearn.domain.member.MemberFixture
import tobyspring.splearn.domain.member.MemberStatus
import tobyspring.splearn.support.AssertThatUtils

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 각 테스트 종료 시 자동 롤백
class MemberApiTest @Autowired constructor(
    private val mvcTester: MockMvcTester,
    private val objectMapper: ObjectMapper,
    private val memberRepository: MemberRepository,
    private val memberRegister: MemberRegister
) {

    @Test
    @DisplayName("Member Register 통합 테스트")
    fun `member register success`() {
        // given
        val request = MemberFixture.createMemberRegisterRequest()
        val requestJson = objectMapper.writeValueAsString(request)

        // when
        val result = mvcTester
            .post().uri("/api/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .exchange()

        // then (HTTP 응답 검증)
        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("$.memberId", AssertThatUtils.notNull())
            .hasPathSatisfying("$.email") { AssertThatUtils.equalsTo(request) }

        // then (DB 검증)
        val body = result.response.contentAsString
        val memberRegisterResponse = objectMapper.readValue<MemberRegisterResponse>(body)
        val member = memberRepository.findById(memberRegisterResponse.memberId)
            ?: error("Member with id ${memberRegisterResponse.memberId} not found")

        assertThat(member.email.address).isEqualTo(request.email)
        assertThat(member.nickname).isEqualTo(request.nickname)
        assertThat(member.status).isEqualTo(MemberStatus.PENDING)
        // 테스트 종료 시 @Transactional에 의해 전체 변경 롤백
    }

    @Test
    @DisplayName("Member Register 통합 테스트 - 이메일 중복")
    fun `member register duplicate email`() {
        // given: 첫 번째 회원 등록 (서비스 직접 호출)
        val request = MemberFixture.createMemberRegisterRequest()
        memberRegister.register(request) // 같은 트랜잭션에 참여 → 테스트 끝나면 롤백

        val requestJson = objectMapper.writeValueAsString(request)

        // when: 동일 이메일로 HTTP 등록 시도
        val result = mvcTester
            .post().uri("/api/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
            .exchange()

        // then
        assertThat(result)
            .apply { print() }
            .hasStatus(HttpStatus.CONFLICT)
    }
}
