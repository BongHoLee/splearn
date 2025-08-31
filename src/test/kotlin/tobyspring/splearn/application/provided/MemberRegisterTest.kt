package tobyspring.splearn.application.provided

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.validation.ConstraintViolationException
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import tobyspring.splearn.domain.DuplicateEmailException
import tobyspring.splearn.domain.MemberFixture
import tobyspring.splearn.domain.MemberRegisterRequest
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
internal class MemberRegisterTest(
    private val memberRegister: MemberRegister,
    private val entityManager: EntityManager
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


        test("RegisterRequest에 정의된 Validation 수행 - password 길이 8 미만이면 실패") {
            notValidException(MemberRegisterRequest("leebongho@gmail.com", "beaoh", "1234")) // password too short
                .constraintViolations.let { violations ->
                    violations.size shouldBe 1
                    violations.first().propertyPath!!.last().toString() shouldBe "password"
                }
        }

        /**
         * 테스트 환경에서는 트랜잭션 범위에서 register, activate가 모두 수행되기 때문에, INSERT 쿼리만 보임
         * 이를 보완하고 실제 INSERT, UPDATE 쿼리가 나가는지 확인하기 위해 entityManager flush, clear를 직접 수행
         */
        test("activate") {
            val member = memberRegister.register(MemberFixture.createMemberRegisterRequest())

            // 강제로 flush, clear 해서 INSERT 쿼리 발생(영속성 컨텍스트 초기화)
            entityManager.flush()
            entityManager.clear()

            val activated = memberRegister.activate(member.id!!)

            // flush를 해줘야 update 쿼리 발생
            entityManager.flush()

            activated.status shouldBe MemberStatus.ACTIVE
        }

    }


    private fun notValidException(invalid: MemberRegisterRequest): ConstraintViolationException {
        return shouldThrowExactly<ConstraintViolationException> {
            memberRegister.register(invalid)
        }
    }
}