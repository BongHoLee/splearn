package tobyspring.splearn.domain.member

import jakarta.persistence.Entity
import org.springframework.util.Assert
import tobyspring.splearn.domain.BaseEntity
import java.time.LocalDateTime

@Entity
class MemberDetail(
    profile: Profile? = null,
    introduction: String? = null,
    registeredAt: LocalDateTime? = null,
    activatedAt: LocalDateTime? = null,
    deactivatedAt: LocalDateTime? = null,
) : BaseEntity() {

    companion object {
        internal fun create(): MemberDetail {
            return MemberDetail(registeredAt = LocalDateTime.now())     // 불변식. 회원 등록 시점에 등록일시가 기록되어야 한다.
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

    internal fun setActivatedAt(atTime: LocalDateTime = LocalDateTime.now()) {
        Assert.isTrue(activatedAt == null, "이미 활성화된 회원입니다.")
        this.activatedAt = atTime
    }

    internal fun deactivate() {
        Assert.isTrue(deactivatedAt == null, "이미 비활성화된 회원입니다.")
        this.deactivatedAt = LocalDateTime.now()
    }

    internal fun updateInfo(updateRequest: MemberInfoUpdateRequest) {
        this.profile = Profile(updateRequest.profileAddress)
        this.introduction = updateRequest.introduction
    }
}
