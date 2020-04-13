package io.github.gabrielshanahan.gazer.api.security

import io.github.gabrielshanahan.gazer.api.security.userdetails.UserDetailsByTokenService
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.SpringSecurityMessageSource
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.util.Assert

@Configuration
class HardcodedAuthenticationProvider(val userDetailsByTokenService: UserDetailsByTokenService) :
    AuthenticationProvider, MessageSourceAware, InitializingBean {

    private val logger = LogFactory.getLog(javaClass)

    private var messages = SpringSecurityMessageSource.getAccessor()

    override fun setMessageSource(messageSource: MessageSource) {
        messages = MessageSourceAccessor(messageSource)
    }

    override fun afterPropertiesSet() {
        Assert.notNull(messages, "A message source must be set")
        Assert.notNull(userDetailsByTokenService, "A UserDetailsService must be set")
    }

    override fun authenticate(authentication: Authentication): Authentication {

        logger.debug("Processing auth in $javaClass")
        Assert.isInstanceOf(HardcodedAuthenticationToken::class.java, authentication
        ) {
            messages.getMessage(
                    "HardcodedAuthenticationProvider.onlySupports",
                    "Only HardcodedAuthenticationToken is supported")
        }

        val hardToken = authentication as HardcodedAuthenticationToken
        val token = hardToken.token

        if (token.isEmpty()) {
            logger.debug("Empty token")
            throw BadCredentialsException(messages.getMessage(
                    "HardcodedAuthenticationProvider.noCredentials",
                    "No token entered"))
        }

        val loadedUser: UserDetails = try {
            logger.debug("Trying to load user by token $token")
            userDetailsByTokenService.loadUserByToken(token)
        } catch (notFound: UsernameNotFoundException) {
            logger.debug("Token '$token' not found")

            throw BadCredentialsException(messages.getMessage(
                    "HardcodedAuthenticationProvider.badCredentials",
                    "Bad credentials"))
        }

        logger.debug("Auth successful, token is $token, loaded user is $loadedUser")
        return HardcodedAuthenticationToken(token, loadedUser)
    }

    override fun supports(authentication: Class<*>): Boolean =
            HardcodedAuthenticationToken::class.java == authentication
}
