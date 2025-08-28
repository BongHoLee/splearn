package tobyspring.splearn.domain

class Member private constructor(
    email: Email,
    nickname: String,
    passwordHash: String,
) {

    var email = email
        private set

    var nickname = nickname
        private set

    var passwordHash = passwordHash
        private set

    var status = MemberStatus.PENDING
        private set

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