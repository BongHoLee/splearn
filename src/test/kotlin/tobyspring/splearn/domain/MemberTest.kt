package tobyspring.splearn.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MemberTest : FunSpec({

    context("회원 생성") {
        test("회원은 기본적으로 PENDING 상태로 생성된다") {
            val member = Member("leebongho@splearn.app", "leebongho", "secret")

            member.status shouldBe MemberStatus.PENDING
        }

        test("회원 생성 시 null 값이 전달되면 예외가 발생한다") {
            val nullString: String? = null

            shouldThrow<NullPointerException> {
                Member(nullString!!, nullString, nullString)
            }
        }
    }

    context("회원 상태 변경") {
        test("PENDING 상태의 회원을 ACTIVE로 변경할 수 있다") {
            val member = Member("leebongho@splearn.app", "leebongho", "secret")

            member.activate()
            member.status shouldBe MemberStatus.ACTIVE
        }

        test("ACTIVE 상태인 회원은 다시 ACTIVATE 할 수 없다.") {
            val member = Member("leebongho@splearn.app", "leebongho", "secret")
            member.activate()

            shouldThrow< IllegalStateException>{
                member.activate()
            }
        }

        test("ACTIVE 상태인 회원은 DEACTIVATE 할 수 있다.") {
            val member = Member("leebongho@splearn.app", "leebongho", "secret")
            member.activate()

            member.deactivate()

            member.status shouldBe MemberStatus.DEACTIVATED
        }

        test("ACTIVE가 아닌 회원은 DEACTIVATE 할 수 없다.") {
            val member = Member("leebongho@splearn.app", "leebongho", "secret")

            shouldThrow< IllegalStateException>{
                member.deactivate()
            }

            member.activate()
            member.deactivate()

            shouldThrow< IllegalStateException>{
                member.deactivate()
            }
        }
    }
})

