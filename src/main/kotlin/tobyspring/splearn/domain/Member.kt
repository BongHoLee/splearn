package tobyspring.splearn.domain

import jakarta.persistence.*
import org.hibernate.annotations.NaturalId
import org.hibernate.annotations.NaturalIdCache

@Entity
@NaturalIdCache
class Member protected constructor(
    id: Long? = null,
    email: Email,
    nickname: String,
    passwordHash: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id = id
        protected set

    @Embedded
    @NaturalId       // 자연키로써 식별성과 고유성(중복 불가)을 보장하기 위함
    var email = email
        protected set

    var nickname = nickname
        protected set

    var passwordHash = passwordHash
        protected set

    @Enumerated(EnumType.STRING)
    var status = MemberStatus.PENDING
        protected set

    companion object {
        fun register(registerRequest: RegisterRequest, passwordEncoder: PasswordEncoder): Member {

            return Member(
                email = Email(registerRequest.email),
                nickname = registerRequest.nickname,
                passwordHash = passwordEncoder.encode(registerRequest.password)
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

    data class RegisterRequest(
        val email: String,
        val nickname: String,
        val password: String
    )
}