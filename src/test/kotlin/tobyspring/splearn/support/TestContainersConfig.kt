package tobyspring.splearn.support

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import tobyspring.splearn.application.required.EmailSender
import tobyspring.splearn.domain.Email
import tobyspring.splearn.domain.PasswordEncoder

/**
 * 테스트 전용 Bean 구성.
 * - 테스트 프로파일에서만 활성 (@Profile("test"))
 * - Fake EmailSender, 단순 PasswordEncoder 제공
 */
@Configuration
internal class TestContainersConfig {
    private val log = LoggerFactory.getLogger(TestContainersConfig::class.java)

    @Bean
    @Primary
    fun testPasswordEncoder(): PasswordEncoder = object : PasswordEncoder {
        override fun encode(password: String): String = "{plain:}$password"
        override fun matches(password: String, passwordHash: String): Boolean =
            password == passwordHash || encode(password) == passwordHash
    }

    @Bean
    @Primary
    fun testEmailSender(): EmailSender = object : EmailSender {
        override fun send(email: Email, subject: String, body: String) {
            log.info("[TestEmailSender] to={}, subject={}, body={}", email.address, subject, body)
        }
    }
}