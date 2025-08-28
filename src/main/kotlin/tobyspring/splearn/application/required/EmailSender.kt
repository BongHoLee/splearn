package tobyspring.splearn.application.required

import tobyspring.splearn.domain.Email

/**
 * 이메일을 발송한다.
 */
interface EmailSender {
    fun send(email: Email, subject: String, body: String)
}