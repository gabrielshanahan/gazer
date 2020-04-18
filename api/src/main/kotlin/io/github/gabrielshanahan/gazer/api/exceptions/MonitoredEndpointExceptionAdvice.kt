package io.github.gabrielshanahan.gazer.api.exceptions

import io.github.gabrielshanahan.gazer.func.into
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
internal class MonitoredEndpointExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(MonitoredEndpointException::class)
    fun handleException(ex: MonitoredEndpointException) = when (ex) {
        is InvalidGazerTokenException -> HttpStatus.UNAUTHORIZED
        is MonitoredEndpointNotFoundException -> HttpStatus.NOT_FOUND
        is MonitoredEndpointForbidden -> HttpStatus.FORBIDDEN
        is InvalidMonitoredEndpoint -> HttpStatus.BAD_REQUEST
    } into {
        ResponseEntity(ex.monitoredEndpointMsg, it)
    }
}
