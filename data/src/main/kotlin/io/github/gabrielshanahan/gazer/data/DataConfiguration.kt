package io.github.gabrielshanahan.gazer.data

import org.h2.tools.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.sql.SQLException

@Configuration
@EnableJpaRepositories
@ComponentScan
class DataConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile("dev")
    @Throws(SQLException::class)
    fun h2Server(): Server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092")
}
