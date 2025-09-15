package tobyspring.splearn.domain.member

import jakarta.persistence.Embeddable


@Embeddable
data class Profile(
    val address: String
) {
    // JPA를 위한 no-arg 생성자 (internal로 제한하여 외부 직접 사용 방지)
    internal constructor() : this(FOR_JPA)

    init {
        // 빈 문자열은 JPA 기본 생성자용이므로 검증에서 제외
        if (address != FOR_JPA) {
            check(PROFILE_ADDRESS_PATTERN.matches(address)) { "프로필 주소 형식이 올바르지 않습니다" }
            check(address.length in 1..15) { "프로필 주소 형식이 올바르지 않습니다" }
        }
    }

    companion object {
        private val PROFILE_ADDRESS_PATTERN = Regex("[a-z0-9]+")
        private const val FOR_JPA = "||_||JPA"
    }

    fun url(): String {
        return "@$address"
    }
}
