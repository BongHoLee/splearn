package tobyspring.splearn.application.member

import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import tobyspring.splearn.application.member.provided.MemberFinder
import tobyspring.splearn.application.member.provided.MemberRegister
import tobyspring.splearn.application.member.required.EmailSender
import tobyspring.splearn.application.member.required.MemberRepository
import tobyspring.splearn.domain.member.*
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

    override fun deactivate(memberId: Long): Member {
        val member = memberFinder.find(memberId)

        member.deactivate()

        return memberRepository.save(member)

    }

    override fun updateInfo(@Valid memberInfoUpdateRequest: MemberInfoUpdateRequest, memberId: Long): Member {
        val member = memberFinder.find(memberId)

        checkDuplicateProfile(member, memberInfoUpdateRequest.profileAddress)

        member.updateInfo(memberInfoUpdateRequest)

        return memberRepository.save(member)
    }

    private fun checkDuplicateProfile(member: Member, profileAddress: String) {
        if (profileAddress.isEmpty()) return
        if(member.detail.profile?.address == profileAddress) return

        memberRepository.findByProfile(Profile(profileAddress))?.let {
            throw DuplicateProfileException("이미 사용중인 프로필 주소입니다 : $profileAddress")
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
