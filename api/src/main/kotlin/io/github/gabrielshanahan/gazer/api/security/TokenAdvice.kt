package io.github.gabrielshanahan.gazer.api.security

import io.github.gabrielshanahan.gazer.api.controller.AuthedController
import io.github.gabrielshanahan.gazer.api.exceptions.InvalidGazerTokenException
import io.github.gabrielshanahan.gazer.api.exceptions.MissingGazerTokenException
import io.github.gabrielshanahan.gazer.api.service.TokenAuthenticationService
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class TokenAdvice(val tokenAuth: TokenAuthenticationService) {

    @Pointcut("within(io.github.gabrielshanahan.gazer.api.controller.AuthedController+)")
    fun isAuthedController() {}

    @Before("@annotation(auth) && isAuthedController() && execution(public * *(..))")
    fun authUser(joinPoint: JoinPoint, auth: Authorized) {
        if (!auth.enabled) {
            return
        }

        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val token = request.getHeader("GazerToken") ?: ""

        if (token.isEmpty()) {
            throw MissingGazerTokenException()
        }

        (joinPoint.target as AuthedController).user = tokenAuth.getUser(token) ?: throw InvalidGazerTokenException()
    }
}
