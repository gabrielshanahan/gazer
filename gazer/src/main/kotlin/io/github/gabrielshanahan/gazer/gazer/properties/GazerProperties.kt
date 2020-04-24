package io.github.gabrielshanahan.gazer.gazer.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties("gazer")
@Component
class GazerProperties {
    var syncRate: Long = 1000
}
