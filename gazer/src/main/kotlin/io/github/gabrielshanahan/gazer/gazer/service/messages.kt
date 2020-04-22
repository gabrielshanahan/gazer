package io.github.gabrielshanahan.gazer.gazer.service

import io.github.gabrielshanahan.gazer.gazer.model.MonitoringResult

sealed class GazerMsg
data class PersistMsg(val result: MonitoringResult): GazerMsg()
