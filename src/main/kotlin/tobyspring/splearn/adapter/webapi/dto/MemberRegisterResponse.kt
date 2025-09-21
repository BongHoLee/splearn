package tobyspring.splearn.adapter.webapi.dto

import tobyspring.splearn.domain.member.Member

data class MemberRegisterResponse(
    val memberId: Long,
    val emailAddress: String,
) {
    companion object {
        fun of(member: Member): MemberRegisterResponse =
            MemberRegisterResponse(
                memberId = member.id,
                emailAddress = member.email.address
            )
    }
}