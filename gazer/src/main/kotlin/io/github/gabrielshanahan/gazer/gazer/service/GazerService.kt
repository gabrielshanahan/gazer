package io.github.gabrielshanahan.gazer.gazer.service

import io.github.gabrielshanahan.gazer.gazer.model.MonitoredEndpoint
import kotlinx.coroutines.channels.SendChannel

interface GazerService {
    suspend fun gaze(endpoint: MonitoredEndpoint, persistor: SendChannel<PersistMsg>)
}
