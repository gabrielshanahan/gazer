package io.github.gabrielshanahan.gazer.gazer

import io.github.gabrielshanahan.gazer.data.DataConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackageClasses = [DataConfiguration::class])
@EntityScan(basePackageClasses = [DataConfiguration::class])
@ComponentScan
class GazerConfiguration
