package io.github.gabrielshanahan.gazer.api.exceptions

import io.github.gabrielshanahan.gazer.func.into
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

/**
 * Controller advice that deals with non-validation exceptions. Returns JSON payload describing the error.
 *
 * @see GazerException
 */
@ControllerAdvice
internal class GazerExceptionAdvice {

    /**
     * Deals with all subclasses of [GazerException]
     */
    @ResponseBody
    @ExceptionHandler(GazerException::class)
    fun handleException(ex: GazerException) = when (ex) {
        is InvalidGazerTokenException -> HttpStatus.UNAUTHORIZED
        is EntityNotFoundException -> HttpStatus.NOT_FOUND
        is EntityForbidden -> HttpStatus.FORBIDDEN
    } into {
        ResponseEntity(ex.gazerMsg, it)
    }
}
