package tobyspring.splearn.domain.shared

data class Email(
    val address: String
) {
    // JPA를 위한 no-arg 생성자 (internal로 제한하여 외부 직접 사용 방지)
    internal constructor() : this("")

    init {
        // 빈 문자열은 JPA 기본 생성자용이므로 검증에서 제외
        if (address.isNotBlank()) {
            check(EMAIL_PATTERN.matches(address)) { "이메일 형식이 올바르지 않습니다: $address" }
        }
    }

    companion object {
        private val EMAIL_PATTERN = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    }
}