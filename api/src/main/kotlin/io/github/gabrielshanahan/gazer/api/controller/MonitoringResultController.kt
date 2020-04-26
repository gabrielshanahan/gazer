package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoringResultResourceAssembler
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoringResultCollectionResponse
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoringResultModelResponse
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoringResultResponseAssembler
import io.github.gabrielshanahan.gazer.api.exceptions.InvalidGazerTokenException
import io.github.gabrielshanahan.gazer.api.exceptions.MonitoringResultNotFoundException
import io.github.gabrielshanahan.gazer.api.model.asModel
import io.github.gabrielshanahan.gazer.data.entity.MonitoringResultEntity
import io.github.gabrielshanahan.gazer.data.repository.MonitoringResultRepository
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Contains endpoints pertaining to MonitoringResults
 */
@RestController
@RequestMapping("/monitoringResults")
class MonitoringResultController(
    val resultRepository: MonitoringResultRepository,
    val resourceAssembler: MonitoringResultResourceAssembler,
    val responseAssembler: MonitoringResultResponseAssembler,
    userRepository: UserRepository
) : AbstractController(userRepository) {

    /**
     * If [token] is valid, returns all MonitoringResults owned by given user, otherwise throws
     * [InvalidGazerTokenException].
     *
     * @see withAuthedUser
     */
    @GetMapping("")
    fun getAll(
        @RequestHeader(value = "GazerToken") token: String
    ): MonitoringResultCollectionResponse = withAuthedUser(token) { user ->
        resultRepository
            .getAllByMonitoredEndpointUserOrderByCheckedDesc(user)
            .map(MonitoringResultEntity::asModel).toMutableList() into
            resourceAssembler::toCollectionModel into responseAssembler::toOkResponse
    }

    /**
     * Checks [token] validity, then returns MonitoringResult given by [id]. Throws one of
     * [InvalidGazerTokenException], [io.github.gabrielshanahan.gazer.api.exceptions.MonitoringResultForbidden] or
     * [MonitoringResultNotFoundException], depending on the situation.
     *
     * @see withAuthedUser
     * @see authAndFind
     */
    @GetMapping("/{id}")
    fun getById(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String
    ): MonitoringResultModelResponse = authAndFind(token, id) { result ->
        result into resourceAssembler::toModel into responseAssembler::toOkResponse
    } orWhenNoneFound { throw MonitoringResultNotFoundException(id) }
}
