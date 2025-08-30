package tobyspring.splearn.domain

class DuplicateEmailException(
    message: String?
) : RuntimeException(message ?: "Email is duplicated")