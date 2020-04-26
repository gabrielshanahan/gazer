package io.github.gabrielshanahan.gazer.api.exceptions

/** Subclass for MonitoringResult */
class MonitoringResultNotFoundException(id: String) :
    EntityNotFoundException("Monitoring result", id)

/** Subclass for MonitoringResult */
class MonitoringResultForbidden(id: String) :
    EntityForbidden("monitoring result", id)
