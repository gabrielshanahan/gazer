package io.github.gabrielshanahan.gazer.api.validation

import io.github.gabrielshanahan.gazer.api.validation.ValidationErrorResponse.Violation
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import javax.validation.ConstraintViolationException

@ControllerAdvice
class ValidationErrorHandlingAdvice {

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun onConstraintValidationException(e: ConstraintViolationException): ValidationErrorResponse =
        e.constraintViolations.map {
            Violation(it.propertyPath.toString(), it.message)
        } into ::ValidationErrorResponse

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun onMethodArgumentNotValidException(e: MethodArgumentNotValidException): ValidationErrorResponse =
        e.bindingResult.fieldErrors.map {
            Violation(it.field, it.defaultMessage ?: "")
        } into ::ValidationErrorResponse
}
