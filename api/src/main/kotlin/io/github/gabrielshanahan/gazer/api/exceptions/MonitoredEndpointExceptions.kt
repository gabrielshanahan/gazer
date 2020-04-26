package io.github.gabrielshanahan.gazer.api.exceptions

/** Subclass for MonitoredEndpoint */
class MonitoredEndpointNotFoundException(id: String) :
    EntityNotFoundException("Monitored endpoint", id)

/** Subclass for MonitoredEndpoint */
class MonitoredEndpointForbidden(id: String) :
    EntityForbidden("monitored endpoint", id)
