package com.yutakomura.infrastructure.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.yutakomura.infrastructure.security.JWTProvider.Companion.CLAIM_ROLES
import com.yutakomura.infrastructure.security.JWTProvider.Companion.X_AUTH_TOKEN
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*

@Component
class JWTProviderImpl : JWTProvider {

    @org.springframework.beans.factory.annotation.Value("\${jwt.secret}")
    private lateinit var secret: String

    @Autowired
    private lateinit var tokenRepository: TokenRepository

    override fun createToken(loginUser: LoginUser): String {
        val now = Date()
        val authToken = JWT.create()
            .withIssuer("com.yutakomura")
            .withIssuedAt(now)
            .withSubject(loginUser.id.toString())
            .withClaim(CLAIM_ROLES, loginUser.authorities.map { it.authority })
            .sign(algorithm())
        val key = loginUser.id.toString()
        val token = Token(Key(key), Value(authToken))
        tokenRepository.insert(token)
        return authToken
    }

    override fun tokenValueFrom(request: jakarta.servlet.ServletRequest): String {
        val token: String? = (request as HttpServletRequest).getHeader(X_AUTH_TOKEN)
        val tokenValue = token?.takeIf { it.startsWith("Bearer ") }?.substring(7)
        tokenValue ?: throw JWTVerificationException("トークンが無効です。")
        return tokenValue
    }

    override fun verifyToken(token: String): DecodedJWT {
        val verifier = JWT.require(algorithm()).build()
        return verifier.verify(token)
    }

    override fun retrieve(decodedJWT: DecodedJWT): LoginUser {
        val userId = decodedJWT.subject.toInt()
        val roles = decodedJWT.getClaim(CLAIM_ROLES).asList(String::class.java)
        return LoginUser(userId, roles.map { SimpleGrantedAuthority(it) })
    }

    private fun algorithm(): Algorithm {
        return Algorithm.HMAC256(secret)
    }
}
