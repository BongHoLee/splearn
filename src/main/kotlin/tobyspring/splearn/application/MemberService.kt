package tobyspring.splearn.application

import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import tobyspring.splearn.application.provided.MemberRegister
import tobyspring.splearn.application.required.EmailSender
import tobyspring.splearn.application.required.MemberRepository
import tobyspring.splearn.domain.DuplicateEmailException
import tobyspring.splearn.domain.Email
import tobyspring.splearn.domain.Member
import tobyspring.splearn.domain.MemberRegisterRequest
import tobyspring.splearn.domain.PasswordEncoder

@Service
@Transactional
@Validated
class MemberService(
    private val memberRepository: MemberRepository,
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
        val member = findByIdOrThrow(memberId)

        member.activate()

        return memberRepository.save(member)
    }

    private fun findByIdOrThrow(memberId: Long): Member {
        return checkNotNull(memberRepository.findById(memberId)) {
            "존재하지 않는 회원입니다. memberId: $memberId"
        }
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