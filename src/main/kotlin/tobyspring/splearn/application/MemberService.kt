package tobyspring.splearn.application

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import tobyspring.splearn.application.provided.MemberRegister
import tobyspring.splearn.application.required.EmailSender
import tobyspring.splearn.application.required.MemberRepository
import tobyspring.splearn.domain.DuplicateEmailException
import tobyspring.splearn.domain.Email
import tobyspring.splearn.domain.Member
import tobyspring.splearn.domain.PasswordEncoder

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
    private val emailSender: EmailSender,
    private val passwordEncoder: PasswordEncoder
) : MemberRegister {

    override fun register(request: Member.RegisterRequest): Member {

        checkDuplicateEmail(request)

        val member = Member.register(registerRequest = request, passwordEncoder = passwordEncoder)

        memberRepository.save(member)

        sendWelcomeEmail(member)

        return member
    }

    private fun checkDuplicateEmail(request: Member.RegisterRequest) {
        memberRepository.findByEmail(Email(request.email))?.let {
            throw DuplicateEmailException("이미 사용중인 이메일 입니다 : ${request.email}")
        }
    }

    private fun sendWelcomeEmail(member: Member) {
        emailSender.send(member.email, "등록을 완료해주세요", "아래 링크를 클릭해서 등록을 완료해주세요.")
    }
}