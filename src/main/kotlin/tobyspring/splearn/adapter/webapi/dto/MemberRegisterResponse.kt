package tobyspring.splearn.adapter.webapi.dto

import tobyspring.splearn.domain.member.Member

data class MemberRegisterResponse(
    val memberId: Long,
    val email: String,
) {
    companion object {
        fun of(member: Member): MemberRegisterResponse =
            MemberRegisterResponse(
                memberId = member.id,
                email = member.email.address
            )
    }
}