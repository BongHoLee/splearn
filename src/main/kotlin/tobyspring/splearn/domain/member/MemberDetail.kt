package tobyspring.splearn.domain.member

import jakarta.persistence.Entity
import org.springframework.util.Assert
import tobyspring.splearn.domain.BaseEntity
import java.time.LocalDateTime

@Entity
class MemberDetail(
    profile: String? = null,
    introduction: String? = null,
    registeredAt: LocalDateTime = LocalDateTime.now(),
    activatedAt: LocalDateTime? = null,
    deactivatedAt: LocalDateTime? = null,
) : BaseEntity() {

    companion object {
        internal fun create(): MemberDetail {
            return MemberDetail()
        }
    }

    var profile = profile
        protected set

    var introduction = introduction
        protected set

    var registeredAt = registeredAt
        protected set

    var activatedAt = activatedAt
        protected set

    var deactivatedAt = deactivatedAt
        protected set

    internal fun setActivatedAt() {
        Assert.isTrue(activatedAt == null, "이미 활성화된 회원입니다.")
        this.activatedAt = LocalDateTime.now()
    }

    internal fun deactivate() {
        Assert.isTrue(deactivatedAt == null, "이미 비활성화된 회원입니다.")
        this.deactivatedAt = LocalDateTime.now()
    }
}