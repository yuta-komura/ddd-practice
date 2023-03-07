package com.yutakomura.infrastructure.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


class JsonRequestAuthenticationFilter(
    private val authenticationManager: AuthenticationManager
) : UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(req: HttpServletRequest, res: HttpServletResponse): Authentication {
        val mapper = ObjectMapper()
        val principal = mapper.readValue(req.inputStream, EmailAndPasswordJsonRequest::class.java)
        val authRequest = UsernamePasswordAuthenticationToken(principal.email, principal.password)
        setDetails(req, authRequest)
        return authenticationManager.authenticate(authRequest)
    }
}
