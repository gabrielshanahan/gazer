package io.github.gabrielshanahan.gazer.gazer.model

internal fun MonitoredEndpoint.toShortStr() = "$name(${id.toString().take(9)}...): $url"
internal fun MonitoringResult.toShortStr() = "$httpStatus(${payload.take(25)}...) at $checked"
