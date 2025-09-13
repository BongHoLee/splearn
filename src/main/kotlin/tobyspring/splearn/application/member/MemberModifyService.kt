package tobyspring.splearn.application.member

import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import tobyspring.splearn.application.member.provided.MemberFinder
import tobyspring.splearn.application.member.provided.MemberRegister
import tobyspring.splearn.application.member.required.EmailSender
import tobyspring.splearn.application.member.required.MemberRepository
import tobyspring.splearn.domain.member.DuplicateEmailException
import tobyspring.splearn.domain.member.Member
import tobyspring.splearn.domain.member.MemberRegisterRequest
import tobyspring.splearn.domain.member.PasswordEncoder
import tobyspring.splearn.domain.shared.Email

@Service
@Transactional
@Validated
class MemberModifyService(
    private val memberRepository: MemberRepository,
    private val memberFinder: MemberFinder,
    private val emailSender: EmailSender,
    private val passwordEncoder: PasswordEncoder,
) : MemberRegister {

    override fun register(@Valid request: MemberRegisterRequest): Member {

        checkDuplicateEmail(request)

        val member = Member.register(memberRegisterRequest = request, passwordEncoder = passwordEncoder)

        memberRepository.save(member)

        sendWelcomeEmail(member)

        return member
    }

    override fun activate(memberId: Long): Member {
        val member = memberFinder.find(memberId)

        member.activate()

        return memberRepository.save(member)
    }

    private fun checkDuplicateEmail(request: MemberRegisterRequest) {
        memberRepository.findByEmail(Email(request.email))?.let {
            throw DuplicateEmailException("이미 사용중인 이메일 입니다 : ${request.email}")
        }
    }

    private fun sendWelcomeEmail(member: Member) {
        emailSender.send(member.email, "등록을 완료해주세요", "아래 링크를 클릭해서 등록을 완료해주세요.")
    }
}
