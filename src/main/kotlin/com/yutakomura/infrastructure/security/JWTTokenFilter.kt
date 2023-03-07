package com.yutakomura.infrastructure.security

import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
import com.yutakomura.ErrorResponse
import com.yutakomura.Log
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import java.util.*

class JWTTokenFilter(
    private val jwtProvider: JWTProviderImpl,
    private val redisTemplate: StringRedisTemplate
) : GenericFilterBean() {

    companion object : Log()

    override fun doFilter(
        req: ServletRequest, res: ServletResponse, chain: FilterChain
    ) {
        try {
            val token = jwtProvider.getToken(req)
            if (token != null) {
                val decodedJWT = jwtProvider.verifyToken(token)
                val loginUser = jwtProvider.retrieve(decodedJWT)
                val tokenRedis = redisTemplate.opsForValue()[loginUser.id.toString()]
                if (!Objects.equals(token, tokenRedis)) throw JWTVerificationException("トークンが無効です。")
                SecurityContextHolder.getContext().authentication =
                    UsernamePasswordAuthenticationToken(loginUser, null, loginUser.authorities)
            }
            chain.doFilter(req, res)
        } catch (e: Exception) {
            val resHttp = res as HttpServletResponse
            resHttp.characterEncoding = Charsets.UTF_8.name()
            resHttp.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
            resHttp.contentType = "application/json"
            log.error(e.message)
            e.printStackTrace()
            resHttp.writer.write(ObjectMapper().writeValueAsString(ErrorResponse(e.message, e.stackTraceToString())))
            resHttp.flushBuffer()
        }
    }
}
