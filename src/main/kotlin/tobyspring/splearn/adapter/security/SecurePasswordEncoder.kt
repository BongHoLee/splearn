package tobyspring.splearn.adapter.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import tobyspring.splearn.domain.member.PasswordEncoder

@Component
class SecurePasswordEncoder(
    private val bCryptPasswordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
) : PasswordEncoder {

    override fun encode(password: String): String {
        return bCryptPasswordEncoder.encode(password)
    }

    override fun matches(password: String, passwordHash: String): Boolean {
        return bCryptPasswordEncoder.matches(password, passwordHash)
    }
}