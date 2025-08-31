package tobyspring.splearn.application.provided

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import org.springframework.test.util.ReflectionTestUtils
import tobyspring.splearn.application.MemberService
import tobyspring.splearn.application.required.EmailSender
import tobyspring.splearn.application.required.MemberRepository
import tobyspring.splearn.domain.Email
import tobyspring.splearn.domain.Member
import tobyspring.splearn.domain.MemberFixture
import tobyspring.splearn.domain.MemberStatus

class MemberRegisterManualTest : FunSpec({

    test("register - stub 사용") {
        val memberRegister = MemberService(
            memberRepository = MemberRepositoryStub(),
            emailSender = EmailSenderStub(),
            passwordEncoder = MemberFixture.createPasswordEncoder()
        )

        val member = memberRegister.register(request = MemberFixture.createMemberRegisterRequest())

        member.id shouldNotBe null
        member.status shouldBe MemberStatus.PENDING
    }

    test("register - 수동 mock 사용 (상호작용 검증)") {
        val emailSenderMock = EmailSenderMock()

        val memberRegister = MemberService(
            memberRepository = MemberRepositoryStub(),
            emailSender = emailSenderMock,
            passwordEncoder = MemberFixture.createPasswordEncoder()
        )

        val member = memberRegister.register(request = MemberFixture.createMemberRegisterRequest())

        member.id shouldNotBe null
        member.status shouldBe MemberStatus.PENDING

        emailSenderMock.tos.shouldHaveSize(1)
        emailSenderMock.tos[0] shouldBe member.email
    }

    test("register - MockK 사용 (상호작용 검증)") {
        val emailSenderMock = mockk<EmailSender>(relaxed = true)

        val memberRegister = MemberService(
            memberRepository = MemberRepositoryStub(),
            emailSender = emailSenderMock,
            passwordEncoder = MemberFixture.createPasswordEncoder()
        )

        val member = memberRegister.register(request = MemberFixture.createMemberRegisterRequest())

        member.id shouldNotBe null
        member.status shouldBe MemberStatus.PENDING
        member.email shouldNotBe null

        verify { emailSenderMock.send(member.email, any(), any()) }
    }
}) {
    private class MemberRepositoryStub : MemberRepository {
        override fun save(member: Member): Member {
            ReflectionTestUtils.setField(member, "id", 1L)
            return member
        }

        override fun findByEmail(email: Email): Member? {
            return null
        }

        override fun findById(memberId: Long): Member? {
            return null
        }
    }

    private class EmailSenderStub : EmailSender {
        override fun send(email: Email, subject: String, body: String) { }
    }

    private class EmailSenderMock : EmailSender {
        val tos: MutableList<Email> = mutableListOf()
        override fun send(email: Email, subject: String, body: String) {
            tos.add(email)
        }

    }
}