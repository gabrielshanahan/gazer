package io.github.gabrielshanahan.gazer.api.security.config

import io.github.gabrielshanahan.gazer.api.security.HardcodedAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint) : WebSecurityConfigurerAdapter() {

    @Bean
    @Throws(java.lang.Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()

    @Bean
    @Throws(java.lang.Exception::class)
    fun hardcodedAuthenticationFilter(): HardcodedAuthenticationFilter =
            HardcodedAuthenticationFilter(authenticationManagerBean(), customAuthenticationEntryPoint)

    @Throws(Exception::class)
    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/h2-console/**", "/actuator/**").permitAll()
                .anyRequest()
                    .authenticated()
                        .and().exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint)
                        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        httpSecurity.addFilterBefore(hardcodedAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
        httpSecurity.headers().frameOptions().disable()
    }

}
