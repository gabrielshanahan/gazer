package io.github.gabrielshanahan.gazer.gazer.service

import io.github.gabrielshanahan.gazer.gazer.model.MonitoringResult

/** Base class for all messages. Currently, there is only one. */
sealed class GazerMsg

/** Signifies that the included [result] should be persisted. */
data class PersistMsg(val result: MonitoringResult) : GazerMsg()
