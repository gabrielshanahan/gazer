package io.github.gabrielshanahan.gazer.api.exceptions

class MonitoringResultNotFoundException(id: String) :
    EntityNotFoundException("Monitoring result", id)

class MonitoringResultForbidden(id: String) :
    EntityForbidden("monitoring result", id)

class InvalidMonitoringResult :
    InvalidEntity("monitoring result")
