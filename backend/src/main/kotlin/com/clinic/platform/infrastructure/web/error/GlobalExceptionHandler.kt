package com.clinic.platform.infrastructure.web.error

import com.clinic.platform.shared.errors.DomainValidationException
import com.clinic.platform.shared.errors.ResourceAlreadyExistsException
import com.clinic.platform.shared.errors.ResourceNotFoundException
import com.clinic.platform.shared.errors.ResourceStateConflictException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(
        ResourceNotFoundException::class
    )
    fun handleNotFound(
        exception: ResourceNotFoundException
    ): ProblemDetail {

        return ProblemDetail
            .forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.message ?: "Resource not found"
            )
    }

    @ExceptionHandler(
        ResourceAlreadyExistsException::class
    )
    fun handleAlreadyExists(
        exception: ResourceAlreadyExistsException
    ): ProblemDetail {

        return ProblemDetail
            .forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.message ?: "Resource already exists"
            )
    }

    @ExceptionHandler(
        ResourceStateConflictException::class
    )
    fun handleStateConflict(
        exception: ResourceStateConflictException
    ): ProblemDetail {

        val problem =
            ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.message
                    ?: "Resource state conflicts with this operation"
            )

        problem.title =
            "Resource state conflict"

        problem.setProperty(
            "code",
            exception.code
        )

        problem.setProperty(
            "currentState",
            exception.currentState
        )

        return problem
    }

    @ExceptionHandler(
        DomainValidationException::class
    )
    fun handleDomainValidation(
        exception: DomainValidationException
    ): ProblemDetail {

        val problem =
            ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                exception.message
                    ?: "Domain validation failed"
            )

        problem.title =
            "Invalid domain value"

        problem.setProperty(
            "code",
            exception.code
        )

        return problem
    }

    @ExceptionHandler(
        MethodArgumentNotValidException::class
    )
    fun handleValidation(
        exception: MethodArgumentNotValidException
    ): ProblemDetail {

        val message =
            exception.bindingResult
                .fieldErrors
                .joinToString("; ") {
                    "${it.field}: ${it.defaultMessage}"
                }

        return ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            message
        )
    }
}
