package tobyspring.splearn.domain

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

/**
 * Kotlin 에서 Bean Validation 을 적용할 때는 use-site target 을 명시해야 한다.
 *
 * 예) @field:Size, @field:Email
 * 그냥 @Size 로만 붙이면 생성자 파라미터(target=PARAMETER)에만 적용되어
 * 객체 필드/프로퍼티 검증(Bean Validation cascade) 시 제약이 인식되지 않는다.
 */
data class MemberRegisterRequest(
    @field:Email val email: String,
    @field:Size(min = 5, max = 20) val nickname: String,
    @field:Size(min = 8, max = 1000) val password: String
)