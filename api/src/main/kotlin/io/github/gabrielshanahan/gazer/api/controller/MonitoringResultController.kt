package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoringResultResourceAssembler
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoringResultCollectionResponse
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoringResultModelResponse
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoringResultResponseAssembler
import io.github.gabrielshanahan.gazer.api.exceptions.MonitoringResultNotFoundException
import io.github.gabrielshanahan.gazer.api.model.User
import io.github.gabrielshanahan.gazer.api.security.Authenticated
import io.github.gabrielshanahan.gazer.api.security.UserAuthentication
import io.github.gabrielshanahan.gazer.api.service.MonitoringResultService
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Contains endpoints pertaining to MonitoringResults
 */
@RestController
@RequestMapping("/monitoringResults")
class MonitoringResultController(
    val resultService: MonitoringResultService,
    val resourceAssembler: MonitoringResultResourceAssembler,
    val responseAssembler: MonitoringResultResponseAssembler
) : UserAuthentication {

    override lateinit var user: User

    /** Returns all MonitoringResults owned by given user. */
    @Authenticated
    @GetMapping("")
    fun getAll(): MonitoringResultCollectionResponse = with(resultService) {
        findAll().toMutableList() into resourceAssembler::toCollectionModel into responseAssembler::toOkResponse
    }

    /**
     * Returns MonitoringResult given by [id].
     *
     * Throws one of [io.github.gabrielshanahan.gazer.api.exceptions.MonitoringResultForbidden] or
     * [MonitoringResultNotFoundException], depending on the situation.
     */
    @Authenticated
    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): MonitoringResultModelResponse = with(resultService) {
        findOwn(id) orWhenNotFound { throw MonitoringResultNotFoundException(id) } into
            resourceAssembler::toModel into responseAssembler::toOkResponse
    }
}
