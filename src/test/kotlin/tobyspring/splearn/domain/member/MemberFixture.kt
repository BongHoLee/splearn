package tobyspring.splearn.domain.member

import tobyspring.splearn.domain.member.MemberRegisterRequest
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties

internal class MemberFixture {
    companion object {
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

        fun createMember(email: String? = null, id: Long = 0L): Member {
            val member = if (email != null) {
                Member.register(createMemberRegisterRequest(email), createPasswordEncoder())
            } else {
                Member.register(createMemberRegisterRequest(), createPasswordEncoder())
            }

            val idField = member.javaClass.superclass.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(member, id)

            return member
        }
    }
}