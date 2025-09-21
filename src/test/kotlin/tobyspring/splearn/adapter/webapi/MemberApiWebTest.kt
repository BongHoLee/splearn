package tobyspring.splearn.adapter.webapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.assertj.MockMvcTester
import tobyspring.splearn.application.member.provided.MemberRegister
import tobyspring.splearn.domain.member.MemberFixture

@WebMvcTest(controllers = [MemberApi::class])
internal class MemberApiWebTest(
    private val mvcTester: MockMvcTester,
    private val objectMapper: ObjectMapper,
) : FunSpec() {

    override fun extensions() = listOf(SpringExtension)

    @MockkBean
    lateinit var memberRegister: MemberRegister // MemberApi가 의존하는 MemberRegister를 MockkBean으로 등록

    init {

        test("register 테스트") {
            val member = MemberFixture.createMember(id = 1L)
            val request = MemberFixture.createMemberRegisterRequest()
            val requestJson = objectMapper.writeValueAsString(request)

            every { memberRegister.register(any()) } returns member

            assertThat(
                mvcTester
                    .post().uri("/api/members").contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.memberId").asNumber().isEqualTo(1)


            verify(exactly = 1) { memberRegister.register(request) }

        }

        test("register request 검증 예외 시 BAD_REQUEST 테스트") {
            val request = MemberFixture.createMemberRegisterRequest(email = "invalid-email")
            val requestJson = objectMapper.writeValueAsString(request)

            assertThat(
                mvcTester
                    .post().uri("/api/members").contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                .hasStatus(HttpStatus.BAD_REQUEST)

        }
    }
}
