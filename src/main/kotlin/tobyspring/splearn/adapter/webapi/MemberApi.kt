package tobyspring.splearn.adapter.webapi

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tobyspring.splearn.adapter.webapi.dto.MemberRegisterResponse
import tobyspring.splearn.application.member.provided.MemberRegister
import tobyspring.splearn.domain.member.MemberRegisterRequest

@RestController
class MemberApi(
    private val memberRegister: MemberRegister
) {

    // register api -> /members POST
    @PostMapping("/api/members")
    fun  register(@RequestBody @Valid memberRegisterRequest: MemberRegisterRequest): MemberRegisterResponse {
        val member = memberRegister.register(memberRegisterRequest)

        return MemberRegisterResponse.of(member)
    }
}