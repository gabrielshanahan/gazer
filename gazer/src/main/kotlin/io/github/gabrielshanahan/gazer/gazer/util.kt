package io.github.gabrielshanahan.gazer.gazer

import io.github.gabrielshanahan.gazer.gazer.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.gazer.model.MonitoringResult

internal fun MonitoredEndpoint.ident() = "$name(${id.toString().take(9)}...): $url"
internal fun MonitoringResult.ident() = "$httpStatus(${payload.take(25)}...) at $checked"
