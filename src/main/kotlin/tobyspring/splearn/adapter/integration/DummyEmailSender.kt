package tobyspring.splearn.adapter.integration

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import tobyspring.splearn.application.required.EmailSender
import tobyspring.splearn.domain.Email

@Component
class DummyEmailSender : EmailSender {
    private val log = LoggerFactory.getLogger(DummyEmailSender::class.java)
    override fun send(email: Email, subject: String, body: String) {
        log.info("this is DummyEmailSender - to={}, subject={}, body={}", email.value, subject, body)
    }
}