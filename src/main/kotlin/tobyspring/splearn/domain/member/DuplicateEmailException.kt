package tobyspring.splearn.domain.member

class DuplicateEmailException(
    message: String?
) : RuntimeException(message ?: "Email is duplicated")