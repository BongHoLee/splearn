package tobyspring.splearn.domain

class Member(
    val email: String,
    val nickname: String,
    val passwordHash: String,
) {
    var status = MemberStatus.PENDING
    private set

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
}