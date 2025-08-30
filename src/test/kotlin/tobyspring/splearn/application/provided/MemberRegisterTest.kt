package tobyspring.splearn.application.provided

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import tobyspring.splearn.domain.DuplicateEmailException
import tobyspring.splearn.domain.MemberFixture
import tobyspring.splearn.domain.MemberStatus
import tobyspring.splearn.support.TestContainersConfig

/**
 * Spring + Kotest 통합 테스트 스켈레톤.
 * - @ActiveProfiles("test") 로 test 전용 설정(application-test.yml) 사용
 * - 필요한 Bean 들이 정상 주입되는지 기본 sanity check
 * - 추가 시나리오: 정상 등록, 중복 이메일, 검증 오류 등을 test { } 블록 추가로 확장
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Import(TestContainersConfig::class)
class MemberRegisterTest(
    private val memberRegister: MemberRegister
) : FunSpec() {
    override fun extensions() = listOf(SpringExtension)

    init {
        test("MemberRegister Bean 이 컨텍스트에 등록되어야 한다") {
            memberRegister shouldNotBe null
        }

        test("회원 등록 테스트 - 추가 시나리오 작성 가능") {
            val member = memberRegister.register(MemberFixture.createMemberRegisterRequest())

            member.id shouldNotBe null
            member.status shouldBe MemberStatus.PENDING
        }


        /**
         * 중복 이메일은 (현재 구현상으로는) 도메인 모델에서 검증할 수 없다.
         * 따라서 이 테스트는 애플리케이션 서비스 테스트를 해야한다.
          */
        test("회원 등록 - 중복 이메일 등록인 경우 예외가 발생") {
            memberRegister.register(MemberFixture.createMemberRegisterRequest())

            shouldThrowExactly<DuplicateEmailException> {
                memberRegister.register(MemberFixture.createMemberRegisterRequest())
            }
        }
    }

}