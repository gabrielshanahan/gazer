package io.github.gabrielshanahan.gazer.gazer

import io.github.gabrielshanahan.gazer.data.DataConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@ComponentScan("io.github.gabrielshanahan.gazer.gazer.service")
@Import(DataConfiguration::class)
class TestConfiguration: GazerConfiguration()
