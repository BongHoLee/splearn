package tobyspring.splearn.application.member

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import tobyspring.splearn.application.member.provided.MemberFinder
import tobyspring.splearn.application.member.required.MemberRepository
import tobyspring.splearn.domain.member.Member

@Service
@Transactional
@Validated
class MemberQueryService(
    private val memberRepository: MemberRepository
) : MemberFinder {

    override fun find(memberId: Long): Member {
        return findByIdOrThrow(memberId)
    }

    private fun findByIdOrThrow(memberId: Long): Member {
        return checkNotNull(memberRepository.findById(memberId)) {
            "존재하지 않는 회원입니다. memberId: $memberId"
        }
    }
}
