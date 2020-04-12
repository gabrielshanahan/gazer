package io.github.gabrielshanahan.gazer.api

import io.github.gabrielshanahan.gazer.data.DataConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan
@EntityScan(basePackageClasses = [DataConfiguration::class])
class ApiConfiguration
