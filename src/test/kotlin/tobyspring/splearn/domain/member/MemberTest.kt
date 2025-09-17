package tobyspring.splearn.domain.member

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import tobyspring.splearn.domain.member.MemberFixture.Companion.createMemberRegisterRequest
import tobyspring.splearn.domain.member.MemberFixture.Companion.createPasswordEncoder

internal class MemberTest : FunSpec({

    lateinit var member: Member
    lateinit var passwordEncoder: PasswordEncoder

    beforeEach {

        passwordEncoder = createPasswordEncoder()
        member = Member.register(
            createMemberRegisterRequest(),
            passwordEncoder
        )
    }

    context("회원 등록") {
        test("회원은 기본적으로 PENDING 상태로 생성된다") {

            member.status shouldBe MemberStatus.PENDING
            member.detail.registeredAt shouldNotBe null
        }
    }

    context("회원 상태 변경") {
        test("PENDING 상태의 회원을 ACTIVE로 변경할 수 있다") {

            member.activate()
            member.status shouldBe MemberStatus.ACTIVE
            member.detail.activatedAt shouldNotBe null
        }

        test("ACTIVE 상태인 회원은 다시 ACTIVATE 할 수 없다.") {
            member.activate()

            shouldThrow<IllegalStateException> {
                member.activate()
            }
        }

        test("ACTIVE 상태인 회원은 DEACTIVATE 할 수 있다.") {
            member.activate()

            member.deactivate()

            member.status shouldBe MemberStatus.DEACTIVATED
            member.detail.deactivatedAt shouldNotBe null
        }

        test("ACTIVE가 아닌 회원은 DEACTIVATE 할 수 없다.") {

            shouldThrow<IllegalStateException> {
                member.deactivate()
            }

            member.activate()
            member.deactivate()

            shouldThrow<IllegalStateException> {
                member.deactivate()
            }
        }
    }

    context("회원 정보 수정") {
        test("회원 정보를 수정할 수 있다") {
            member.activate()

            val updateRequest = MemberInfoUpdateRequest(
                nickname = "newnickname",
                profileAddress = "nicknameaddr",
                introduction = "Hello, I'm new here!"
            )

            member.updateInfo(updateRequest)

            member.nickname shouldBe updateRequest.nickname
            member.detail.profile?.address shouldBe updateRequest.profileAddress
            member.detail.introduction shouldBe updateRequest.introduction
        }

        test("회원정보 수정은 ACTIVE 상태에서만 가능하다") {
            val updateRequest = MemberInfoUpdateRequest(
                nickname = "newnickname",
                profileAddress = "nicknameaddr",
                introduction = "Hello, I'm new here!"
            )

            shouldThrow<IllegalStateException> {
                member.updateInfo(updateRequest)
            }

        }
    }

    context("비밀번호 검증") {
        test("비밀번호가 일치하면 true를 반환한다") {
            member.verifyPassword("verysecret2", passwordEncoder) shouldBe true
        }

        test("비밀번호가 일치하지 않으면 false를 반환한다") {
            member.verifyPassword("wrongpassword", passwordEncoder) shouldBe false
        }
    }

    context("속성 변경") {
        test("패스워드를 변경할 수 있다.") {
            member.changePassword("new secret", passwordEncoder)

            member.verifyPassword("new secret", passwordEncoder = passwordEncoder)
        }
    }

    context("email 검증") {
        test("유효하지 않은 이메일 패턴은 예외를 발생시킨다.") {
            shouldThrow<IllegalStateException> {
                Member.register(createMemberRegisterRequest("invalid-email"), passwordEncoder)
            }

            shouldNotThrowAny {
                Member.register(createMemberRegisterRequest(), passwordEncoder)
            }
        }
    }
})


