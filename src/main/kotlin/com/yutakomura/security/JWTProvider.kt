package com.yutakomura.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*

@Component
class JWTProvider(
    @Value("\${jwt.secret}") private val secret: String,
    private val redisTemplate: StringRedisTemplate
) {

    val algorithm: Algorithm = Algorithm.HMAC256(secret)

    fun createToken(user: LoginUser): String {
        val now = Date()
        val authToken = JWT.create()
            .withIssuer("com.yutakomura")
            .withIssuedAt(now)
            .withSubject(user.id.toString())
            .withClaim(CLAIM_ROLES, user.authorities.map { it.authority })
            .sign(algorithm)
        val key = user.id.toString()
        redisTemplate.opsForValue()[key] = authToken
        return authToken
    }

    fun getToken(request: jakarta.servlet.ServletRequest?): String? {
        val token: String? = (request as HttpServletRequest).getHeader(X_AUTH_TOKEN)
        return token?.takeIf { it.startsWith("Bearer ") }?.substring(7)
    }

    fun verifyToken(token: String): DecodedJWT {
        val verifier = JWT.require(algorithm).build()
        return verifier.verify(token)
    }

    fun retrieve(decodedJWT: DecodedJWT): LoginUser {
        val userId = decodedJWT.subject.toInt()
        val roles = decodedJWT.getClaim(CLAIM_ROLES).asList(String::class.java)
        return LoginUser(userId, roles.map { SimpleGrantedAuthority(it) })
    }

    companion object {
        const val X_AUTH_TOKEN = "X-AUTH-TOKEN"
        const val CLAIM_ROLES = "roles"
    }
}
