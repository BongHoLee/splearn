package tobyspring.splearn.application.provided

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import tobyspring.splearn.domain.MemberFixture
import tobyspring.splearn.support.TestContainersConfig

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Import(TestContainersConfig::class)
internal class MemberFinderTest(
    private val memberRegister: MemberRegister,
    private val memberFinder: MemberFinder,
    private val entityManager: EntityManager
) : FunSpec() {
    override fun extensions() = listOf(SpringExtension)

    init {
        test("회원 조회 성공 테스트") {
            val member = memberRegister.register(MemberFixture.createMemberRegisterRequest())
            flushAndClear()

            val find = memberFinder.find(member.id!!)

            find.id shouldBe member.id
        }

        test("회원 조회 실패 테스트") {

            shouldThrowExactly<IllegalStateException> {
                memberFinder.find(99999L)
            }
        }
    }

    fun flushAndClear() {
        entityManager.flush()
        entityManager.clear()
    }
}