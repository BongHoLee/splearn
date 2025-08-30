package tobyspring.splearn.application

import org.springframework.stereotype.Service
import tobyspring.splearn.application.provided.MemberRegister
import tobyspring.splearn.application.required.EmailSender
import tobyspring.splearn.application.required.MemberRepository
import tobyspring.splearn.domain.Member
import tobyspring.splearn.domain.PasswordEncoder

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val emailSender: EmailSender,
    private val passwordEncoder: PasswordEncoder
) : MemberRegister {
    override fun register(request: Member.RegisterRequest): Member {

        // check

        // domain model
        val member = Member.register(registerRequest = request, passwordEncoder = passwordEncoder)

        // repository
        memberRepository.save(member)

        // post process
        emailSender.send(member.email, "등록을 완료해주세요", "아래 링크를 클릭해서 등록을 완료해주세요.")

        return member
    }
}