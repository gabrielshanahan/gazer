package io.github.gabrielshanahan.gazer.api.security

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.web.filter.OncePerRequestFilter

class HardcodedAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
    private val authenticationEntryPoint: AuthenticationEntryPoint
) : OncePerRequestFilter() {

    private val failureHandler: AuthenticationFailureHandler =
        AuthenticationEntryPointFailureHandler(authenticationEntryPoint)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        // Ugh, this sucks, but we really need to move on
        if (request.requestURI.contains("h2-console") || request.requestURI.contains("actuator")) {
            SecurityContextHolder.getContext().authentication = HardcodedAuthenticationToken(
                "dcb20f8a-5657-4f1b-9f7f-ce65739b359e",
                User("Batman", "dcb20f8a-5657-4f1b-9f7f-ce65739b359e", emptyList())
            )
        } else {
            val requestToken: String = request.getHeader("MyCustomToken") ?: ""

            val authRequest = HardcodedAuthenticationToken(requestToken)

            try {
                SecurityContextHolder.getContext().authentication = authenticationManager.authenticate(authRequest)
            } catch (failed: InternalAuthenticationServiceException) {
                logger.error(
                    "An internal error occurred while trying to authenticate the user.",
                    failed)
                failureHandler.onAuthenticationFailure(request, response, failed)
                return
            } catch (failed: AuthenticationException) {
                failureHandler.onAuthenticationFailure(request, response, failed)
                return
            }
        }
        filterChain.doFilter(request, response)
    }
}
