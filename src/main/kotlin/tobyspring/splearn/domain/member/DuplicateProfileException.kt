package tobyspring.splearn.domain.member

class DuplicateProfileException(
    message: String?
) : RuntimeException(message ?: "Profile is duplicated")