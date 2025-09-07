package tobyspring.splearn.adapter.integration

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Fallback
import org.springframework.stereotype.Component
import tobyspring.splearn.application.required.EmailSender
import tobyspring.splearn.domain.Email

@Component
@Fallback
class DummyEmailSender : EmailSender {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override fun send(email: Email, subject: String, body: String) {
        logger.info("this is DummyEmailSender - to={}, subject={}, body={}", email.address, subject, body)
    }
}