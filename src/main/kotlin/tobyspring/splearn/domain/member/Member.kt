package tobyspring.splearn.domain.member

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToOne
import org.hibernate.annotations.NaturalId
import tobyspring.splearn.domain.BaseEntity
import tobyspring.splearn.domain.shared.Email

@Entity
class Member(
    email: Email,
    nickname: String,
    passwordHash: String,
    detail: MemberDetail,
) : BaseEntity() {

    @NaturalId       // 자연키로써 식별성과 고유성(중복 불가)을 보장하기 위함
    var email = email
        protected set

    var nickname = nickname
        protected set

    var passwordHash = passwordHash
        protected set

    var status = MemberStatus.PENDING
        protected set

    @OneToOne(fetch = FetchType.LAZY, cascade = [(CascadeType.ALL)])
    var detail = detail
        protected set

    companion object {
        fun register(memberRegisterRequest: MemberRegisterRequest, passwordEncoder: PasswordEncoder): Member {
            return Member(
                email = Email(memberRegisterRequest.email),
                nickname = memberRegisterRequest.nickname,
                passwordHash = passwordEncoder.encode(memberRegisterRequest.password),
                detail = MemberDetail.create()
            )
        }
    }

    fun activate() {
        check(this.status == MemberStatus.PENDING) {
            "회원은 PENDING 상태에서만 활성화할 수 있습니다."
        }

        this.status = MemberStatus.ACTIVE
        this.detail.setActivatedAt()
    }


    fun deactivate() {
        check(this.status == MemberStatus.ACTIVE) {
            "회원은 ACTIVE 상태에서만 비활성화할 수 있습니다."
        }

        this.status = MemberStatus.DEACTIVATED
        this.detail.deactivate()
    }

    fun isActive(): Boolean = this.status == MemberStatus.ACTIVE

    fun verifyPassword(password: String, passwordEncoder: PasswordEncoder): Boolean {
        return passwordEncoder.matches(password, this.passwordHash)
    }

    fun changeNickname(nickname: String) {
        this.nickname = nickname
    }

    fun updateInfo(updateRequest: MemberInfoUpdateRequest) {
        this.nickname = updateRequest.nickname

        // 도메인 로직 상 한번에 보내는게 더 자연스럽다 - 변경이 한 번에 일어날 수 있는 일이기 떄문에.
        this.detail.updateInfo(updateRequest)
    }

    fun changePassword(password: String, passwordEncoder: PasswordEncoder) {
        this.passwordHash = passwordEncoder.encode(password)
    }

}

