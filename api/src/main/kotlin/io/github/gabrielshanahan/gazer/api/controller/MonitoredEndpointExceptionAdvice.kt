package io.github.gabrielshanahan.gazer.api.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
internal class MonitoredEndpointExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(MonitoredEndpointController.InvalidGazerTokenException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun invalidGazerTokenHandler(ex: MonitoredEndpointController.InvalidGazerTokenException): String {
        return ex.message!!
    }

    @ResponseBody
    @ExceptionHandler(MonitoredEndpointController.MonitoredEndpointNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun monitoredEndpointNotFoundHandler(ex: MonitoredEndpointController.MonitoredEndpointNotFoundException): String {
        return ex.message!!
    }
}
