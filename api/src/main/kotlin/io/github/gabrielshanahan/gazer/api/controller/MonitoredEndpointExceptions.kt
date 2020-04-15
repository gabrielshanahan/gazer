package io.github.gabrielshanahan.gazer.api.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

open class MonitoredEndpointException(val monitoredEndpointMsg: String) :
    RuntimeException(monitoredEndpointMsg)

class InvalidGazerTokenException :
    MonitoredEndpointException("Invalid GazerToken")
class MonitoredEndpointNotFoundException(id: String) :
    MonitoredEndpointException("Monitored endpoint $id not found")
class MonitoredEndpointForbidden(id: String) :
    MonitoredEndpointException("You do not have permission to access monitored endpoint $id")
class InvalidMonitoredEndpoint :
    MonitoredEndpointException("Payload does not represent a valid MonitoredEndpoint")

@ControllerAdvice
internal class MonitoredEndpointExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(MonitoredEndpointException::class)
    fun handleException(ex: MonitoredEndpointException) = ResponseEntity(
        ex.monitoredEndpointMsg,
        when (ex) {
            is InvalidGazerTokenException -> HttpStatus.UNAUTHORIZED
            is MonitoredEndpointNotFoundException -> HttpStatus.NOT_FOUND
            is MonitoredEndpointForbidden -> HttpStatus.FORBIDDEN
            is InvalidMonitoredEndpoint -> HttpStatus.BAD_REQUEST
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
    )
}
