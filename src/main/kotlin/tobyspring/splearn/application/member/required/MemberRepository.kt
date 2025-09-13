package tobyspring.splearn.application.member.required

import org.springframework.data.repository.Repository
import tobyspring.splearn.domain.shared.Email
import tobyspring.splearn.domain.member.Member

/**
 * 회원 정보를 저장하거나 조회한다.
 */
interface MemberRepository : Repository<Member, Long> {
    fun save(member: Member): Member
    fun findByEmail(email: Email): Member?
    fun findById(memberId: Long): Member?
}