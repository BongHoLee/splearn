package tobyspring.splearn.domain

import jakarta.persistence.*
import org.hibernate.annotations.NaturalId
import org.hibernate.annotations.NaturalIdCache

@Entity
@Table(name = "member", uniqueConstraints = [UniqueConstraint(name = "uk_member_email", columnNames = ["email_address"])])
@NaturalIdCache
class Member (
    email: Email,
    nickname: String,
    passwordHash: String,
) : BaseEntity() {

    @Embedded
    @NaturalId       // 자연키로써 식별성과 고유성(중복 불가)을 보장하기 위함
    var email = email
        protected set

    @Column(length = 100, nullable = false)
    var nickname = nickname
        protected set

    @Column(length = 200, nullable = false)
    var passwordHash = passwordHash
        protected set

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    var status = MemberStatus.PENDING
        protected set

    companion object {
        fun register(memberRegisterRequest: MemberRegisterRequest, passwordEncoder: PasswordEncoder): Member {

            return Member(
                email = Email(memberRegisterRequest.email),
                nickname = memberRegisterRequest.nickname,
                passwordHash = passwordEncoder.encode(memberRegisterRequest.password)
            )
        }

    }

    fun activate() {
        check(this.status == MemberStatus.PENDING) {
            "회원은 PENDING 상태에서만 활성화할 수 있습니다."
        }

        this.status = MemberStatus.ACTIVE
    }


    fun deactivate() {
        check(this.status == MemberStatus.ACTIVE) {
            "회원은 ACTIVE 상태에서만 비활성화할 수 있습니다."
        }

        this.status = MemberStatus.DEACTIVATED
    }

    fun isActive(): Boolean = this.status == MemberStatus.ACTIVE

    fun verifyPassword(password: String, passwordEncoder: PasswordEncoder): Boolean {
        return passwordEncoder.matches(password, this.passwordHash)
    }

    fun changeNickname(nickname: String) {
        this.nickname = nickname
    }

    fun changePassword(password: String, passwordEncoder: PasswordEncoder) {
        this.passwordHash = passwordEncoder.encode(password)
    }

}