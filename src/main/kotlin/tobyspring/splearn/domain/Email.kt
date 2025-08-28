package tobyspring.splearn.domain

import jakarta.persistence.Embeddable

@Embeddable
data class Email(val value: String) {
    init {
        check(value.isNotBlank()) { "이메일은 비어있을 수 없습니다." }
        check(EMAIL_PATTERN.matches(value)) { "이메일 형식이 올바르지 않습니다: $value" }
    }

    companion object {
        private val EMAIL_PATTERN = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    }
}