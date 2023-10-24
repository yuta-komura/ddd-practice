package com.yutakomura.infrastructure.security

import com.auth0.jwt.exceptions.JWTVerificationException
import com.yutakomura.infrastructure.Container
import com.yutakomura.infrastructure.log.Log
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean

class JWTTokenFilter(
    private val jwtProvider: JWTProvider,
) : GenericFilterBean() {

    companion object : Log()

    private val tokenRepository = Container.getBean(TokenRepository::class.java)

    override fun doFilter(
        request: ServletRequest, response: ServletResponse, filterChain: FilterChain
    ) {
        val requestURI = (request as HttpServletRequest).requestURI
        if (requestURI == "/api/login" || requestURI == "/error") {
            filterChain.doFilter(request, response)
            return
        }
        val token = jwtProvider.tokenValueFrom(request)
        val decodedJWT = jwtProvider.verifyToken(token)
        val loginUser = jwtProvider.retrieve(decodedJWT)
        val tokenRedis = tokenRepository.selectBy(Key(loginUser.id.toString()))
        if (token != tokenRedis?.value?.value) {
            throw JWTVerificationException("トークンが無効です。")
        }
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(loginUser, null, loginUser.authorities)
        filterChain.doFilter(request, response)
    }
}

