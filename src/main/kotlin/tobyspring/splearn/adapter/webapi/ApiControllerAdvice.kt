package tobyspring.splearn.adapter.webapi

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import tobyspring.splearn.domain.member.DuplicateEmailException
import java.time.LocalDateTime

@ControllerAdvice
class ApiControllerAdvice : ResponseEntityExceptionHandler() {

    // 예외 발생에 대한 표준인 Problem Detail을 활용하여 응답 본문에 예외 메시지를 포함
    @ExceptionHandler(DuplicateEmailException::class)
    fun emailExceptionHandler(ex: DuplicateEmailException): ProblemDetail {
        return getProblemDetail(status = HttpStatus.CONFLICT, ex = ex)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ProblemDetail {
        return  getProblemDetail(ex)
    }

    private fun getProblemDetail(ex: Exception, status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, ex.message)

        problemDetail.setProperty("timestamp", LocalDateTime.now())
        problemDetail.setProperty("exception", ex::class.simpleName)

        return problemDetail
    }

}