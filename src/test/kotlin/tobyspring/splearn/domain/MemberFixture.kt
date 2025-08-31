package tobyspring.splearn.domain

internal class MemberFixture {
    companion object{
        fun createMemberRegisterRequest(email: String = "leebongho@splearn.com"): MemberRegisterRequest {
            return MemberRegisterRequest(
                email = email,
                nickname = "leebongho",
                password = "verysecret2"
            )
        }

        fun createPasswordEncoder(): PasswordEncoder = object : PasswordEncoder {
            override fun encode(password: String): String = password.uppercase()
            override fun matches(password: String, passwordHash: String): Boolean = encode(password) == passwordHash
        }
    }
}