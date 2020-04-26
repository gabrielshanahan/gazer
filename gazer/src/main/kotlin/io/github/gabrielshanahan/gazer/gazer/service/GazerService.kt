package io.github.gabrielshanahan.gazer.gazer.service

import io.github.gabrielshanahan.gazer.gazer.model.MonitoredEndpoint
import kotlinx.coroutines.channels.SendChannel

/**
 * Defines gazing functionality. Currently, only one implementation exists, but it is conceivable that in the future,
 * a different implementation would actually dispatch the gazing to different "actors" residing in different network
 * locations, to test not only availability, but availability from different locations.
 *
 * @see SimpleGazerService
 */
interface GazerService {
    /** Expected to send an HTTP request to [endpoint] and send the result to [persistor]. */
    suspend fun gaze(endpoint: MonitoredEndpoint, persistor: SendChannel<PersistMsg>)
}
