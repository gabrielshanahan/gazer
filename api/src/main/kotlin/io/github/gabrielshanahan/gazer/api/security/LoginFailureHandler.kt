package io.github.gabrielshanahan.gazer.api.security

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.apache.commons.logging.LogFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationFailureHandler

class LoginFailureHandler : AuthenticationFailureHandler {

    private val logger = LogFactory.getLog(javaClass)

    data class FailureResponse(val message: String)

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(
        httpServletRequest: HttpServletRequest?,
        httpServletResponse: HttpServletResponse,
        e: AuthenticationException
    ) {
        sendError(httpServletResponse, e.localizedMessage)
    }

    @Throws(IOException::class)
    private fun sendError(response: HttpServletResponse, message: String) {
        SecurityContextHolder.clearContext()

        response.apply {
            contentType = "application/json"
            status = HttpServletResponse.SC_UNAUTHORIZED
            writer.println(toJson(FailureResponse(message)))
            writer.flush()
        }
    }

    @Throws(JsonProcessingException::class)
    fun toJson(response: FailureResponse): String {
        val ow: ObjectWriter = ObjectMapper().writer().withDefaultPrettyPrinter()
        return try {
            ow.writeValueAsString(response)
        } catch (e: JsonProcessingException) {
            logger.error(e.localizedMessage)
            throw e
        }
    }
}
