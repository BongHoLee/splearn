package tobyspring.splearn.domain.shared

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual

internal class EmailTest : FunSpec({
    context(" 이메일에 대한 동등성 검사") {
        test("같은 이메일 값은 동등하다") {
            val email1 = Email("leebongho@naver.com")
            val email2 = Email("leebongho@naver.com")

            email1 shouldBeEqual email2
        }
    }
})