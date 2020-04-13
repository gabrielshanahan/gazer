package io.github.gabrielshanahan.gazer.api.security.config

import io.github.gabrielshanahan.gazer.api.security.LoginFailureHandler
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {
    val failureHandler = LoginFailureHandler()

    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {

        failureHandler.onAuthenticationFailure(request, response, authException)
    }
}
