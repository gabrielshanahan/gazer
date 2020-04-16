package io.github.gabrielshanahan.gazer.api.exceptions

sealed class MonitoredEndpointException(val monitoredEndpointMsg: String) :
    RuntimeException(monitoredEndpointMsg)

class InvalidGazerTokenException :
    MonitoredEndpointException("Invalid GazerToken")

class MonitoredEndpointNotFoundException(id: String) :
    MonitoredEndpointException("Monitored endpoint $id not found")

class MonitoredEndpointForbidden(id: String) :
    MonitoredEndpointException("You do not have permission to access monitored endpoint $id")

class InvalidMonitoredEndpoint :
    MonitoredEndpointException("Payload does not represent a valid MonitoredEndpoint")
