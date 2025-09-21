package tobyspring.splearn.application.member

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
import tobyspring.splearn.application.member.required.MemberRepository
import tobyspring.splearn.domain.member.Member
import tobyspring.splearn.domain.member.MemberFixture.Companion.createMemberRegisterRequest
import tobyspring.splearn.domain.member.MemberFixture.Companion.createPasswordEncoder
import tobyspring.splearn.domain.member.MemberStatus

@DataJpaTest
internal class MemberRepositoryTest {

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var entityManager: EntityManager

    @Test
    fun `create Member test`() {
        val member = Member.register(createMemberRegisterRequest(), createPasswordEncoder())

        assertThat(member.id).isEqualTo(0L)

        memberRepository.save(member)

        assertThat(member.id).isNotNull()

        // save 했다고 해서 save 되는게 아닐 수 있다(flush 되지 않고 SQL 실행이 안될 수 있다)
        // 그래서 강제로 flush 를 해준다
        entityManager.flush()
        entityManager.clear()

        val found = memberRepository.findById(member.id)!!
        found.status shouldBe MemberStatus.PENDING
        found.detail.registeredAt shouldNotBe null      // MemberDetail이 Cascading 되어있는지 여부에 대한 테스트를 위함.

    }

    @Test
    fun duplicateEmailFailTest() {
        val member1 = Member.register(createMemberRegisterRequest(), createPasswordEncoder())
        memberRepository.save(member1)

        val member2 = Member.register(createMemberRegisterRequest(), createPasswordEncoder())


        assertThatThrownBy {
            memberRepository.save(member2)
        }.isInstanceOf(DataIntegrityViolationException::class.java)

    }
}