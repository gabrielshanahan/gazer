package io.github.gabrielshanahan.gazer.api.exceptions

class MonitoredEndpointNotFoundException(id: String) :
    EntityNotFoundException("Monitored endpoint", id)

class MonitoredEndpointForbidden(id: String) :
    EntityForbidden("monitored endpoint", id)
