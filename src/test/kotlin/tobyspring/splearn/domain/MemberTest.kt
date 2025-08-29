package tobyspring.splearn.domain

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import tobyspring.splearn.domain.MemberFixture.Companion.createMemberRegisterRequest
import tobyspring.splearn.domain.MemberFixture.Companion.createPasswordEncoder

class MemberTest : FunSpec({

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
        }
    }

    context("회원 상태 변경") {
        test("PENDING 상태의 회원을 ACTIVE로 변경할 수 있다") {

            member.activate()
            member.status shouldBe MemberStatus.ACTIVE
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

    context("비밀번호 검증") {
        test("비밀번호가 일치하면 true를 반환한다") {
            member.verifyPassword("secret", passwordEncoder) shouldBe true
        }

        test("비밀번호가 일치하지 않으면 false를 반환한다") {
            member.verifyPassword("wrongpassword", passwordEncoder) shouldBe false
        }
    }

    context("속성 변경") {
        test("닉네임을 변경할 수 있다.") {
            member.nickname shouldBe "leebongho"

            member.changeNickname("beaoh")

            member.nickname shouldBe "beaoh"
        }

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


