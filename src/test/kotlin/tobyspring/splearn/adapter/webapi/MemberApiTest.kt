package tobyspring.splearn.adapter.webapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import org.assertj.core.api.Assertions.assertThat
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
@Transactional
internal class MemberApiTest(
    private val mvcTester: MockMvcTester,
    private val objectMapper: ObjectMapper,
    private val memberRepository: MemberRepository,
    private val memberRegister: MemberRegister
) : FunSpec() {


    init {
        test("Member Register 통합 테스트") {
            val request = MemberFixture.createMemberRegisterRequest()
            val requestJson = objectMapper.writeValueAsString(request)

            val result = mvcTester
                .post().uri("/api/members").contentType(MediaType.APPLICATION_JSON)
                .content(requestJson).exchange()

            assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.memberId", AssertThatUtils.notNull())
                .hasPathSatisfying("$.email") { AssertThatUtils.equalsTo(request) }


            val memberRegisterResponse = objectMapper.readValue<MemberRegisterResponse>(result.response.contentAsString)
            val member = memberRepository.findById(memberRegisterResponse.memberId) ?: throw IllegalStateException("Member with id ${memberRegisterResponse.memberId} not found")

            member.email.address shouldBeEqual request.email
            member.nickname shouldBeEqual request.nickname
            member.status shouldBeEqual MemberStatus.PENDING
        }

        test("Member Register 통합 테스트 - 이메일 중복") {
            // 첫 번째 회원 등록
            val request = MemberFixture.createMemberRegisterRequest()
            memberRegister.register(request)

            val requestJson = objectMapper.writeValueAsString(request)

            // 두 번째 회원 등록 시도 (이메일 중복)
            val result = mvcTester.post().uri("/api/members").contentType(MediaType.APPLICATION_JSON)
                .content(requestJson).exchange()

            assertThat(result)
                .apply(print())
                .hasStatus(HttpStatus.CONFLICT)
        }
    }


}
