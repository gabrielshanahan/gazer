package io.github.gabrielshanahan.gazer.api.exceptions

import io.github.gabrielshanahan.gazer.func.into
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
internal class GazerExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(GazerException::class)
    fun handleException(ex: GazerException) = when (ex) {
        is InvalidGazerTokenException -> HttpStatus.UNAUTHORIZED
        is EntityNotFoundException -> HttpStatus.NOT_FOUND
        is EntityForbidden -> HttpStatus.FORBIDDEN
        is InvalidEntity -> HttpStatus.BAD_REQUEST
    } into {
        ResponseEntity(ex.gazerMsg, it)
    }
}
