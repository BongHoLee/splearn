package tobyspring.splearn.domain

class MemberFixture {
    companion object{
        fun createMemberRegisterRequest(email: String = "leebongho@splearn.com"): MemberRegisterRequest {
            return MemberRegisterRequest(
                email = email,
                nickname = "leebongho",
                password = "secret"
            )
        }

        fun createPasswordEncoder(): PasswordEncoder = object : PasswordEncoder {
            override fun encode(password: String): String = password.uppercase()
            override fun matches(password: String, passwordHash: String): Boolean = encode(password) == passwordHash
        }
    }
}