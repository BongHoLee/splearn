package tobyspring.splearn.domain

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class MemberRegisterRequest(
    @Email val email: String,
    @Size(min = 5, max = 20) val nickname: String,
    @Size(min = 8, max = 1000) val password: String
)