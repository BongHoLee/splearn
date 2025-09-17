package tobyspring.splearn.domain.member

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ProfileTest : FunSpec({

    context("프로필 정상 생성") {
        test("올바른 형식의 주소로 프로필을 생성할 수 있다") {
            val validAddress = "validaddress123"
            val profile = Profile(address = validAddress)
            profile.address shouldBe validAddress
        }

        test("url 생성시 @가 붙는다") {
            val profile = Profile(address = "validaddress")
            profile.url() shouldBe "@validaddress"
        }

        test("빈 문자열인 경우 정상 생성(프로필 지우기)") {
            val profile = Profile(address = "")
            profile.url() shouldBe "@"
        }
    }
    context("프로필 생성 실패") {
        test("15자 이상인 경우예외 발생") {
            shouldThrowExactly<IllegalStateException> { Profile(address = "12345678980123456578") }
                .message shouldBe "프로필 주소 형식이 올바르지 않습니다"
        }

        test("소문자 또는 숫자만으로 구성되지 않은 경우 예외 발생") {
            shouldThrowExactly<IllegalStateException> { Profile(address = "프로필") }
                .message shouldBe "프로필 주소 형식이 올바르지 않습니다"

            shouldThrowExactly<IllegalStateException> { Profile(address = "A") }
                .message shouldBe "프로필 주소 형식이 올바르지 않습니다"
        }
    }
})
