package com.yutakomura.infrastructure.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.yutakomura.infrastructure.security.JWTProvider.Companion.CLAIM_ROLES
import com.yutakomura.infrastructure.security.JWTProvider.Companion.X_AUTH_TOKEN
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class JWTProviderTest {

    @Autowired
    private lateinit var jwtProvider: JWTProvider

    @MockBean
    private lateinit var tokenRepository: TokenRepository

    private val request = mock(HttpServletRequest::class.java)

    @Test
    fun `createTokenメソッドは有効なトークンを生成し、保存できること`() {
        val id = 1
        val role = "ROLE_USER"
        val expectedLoginUser = LoginUser(id, listOf(SimpleGrantedAuthority(role)))
        val authToken = jwtProvider.createToken(expectedLoginUser)
        val token = Token(Key(id.toString()), Value(authToken))
        val decodedJWT: DecodedJWT = jwtProvider.verifyToken(authToken)
        val actualLoginUser: LoginUser = jwtProvider.retrieve(decodedJWT)
        assertEquals(expectedLoginUser, actualLoginUser)
        verify(tokenRepository, times(1)).insert(token)
    }

    @Test
    fun `tokenValueFromメソッドはヘッダー(X_AUTH_TOKEN)からトークン値を取得すること`() {
        `when`(request.getHeader(X_AUTH_TOKEN)).thenReturn("Bearer abc123")
        val token = jwtProvider.tokenValueFrom(request)
        assertEquals("abc123", token)
    }

    @Test
    fun `tokenValueFromメソッドはヘッダー(X_AUTH_TOKEN)がない場合はJWTVerificationExceptionをスローすること`() {
        `when`(request.getHeader(X_AUTH_TOKEN)).thenReturn(null)
        assertThrows<JWTVerificationException> {
            jwtProvider.tokenValueFrom(request)
        }
    }

    @Test
    fun `tokenValueFromメソッドは不正な形式(X_AUTH_TOKEN)のヘッダーの場合はJWTVerificationExceptionをスローすること`() {
        `when`(request.getHeader(X_AUTH_TOKEN)).thenReturn("abc123")
        assertThrows<JWTVerificationException> {
            jwtProvider.tokenValueFrom(request)
        }
    }

    @Test
    fun verifyTokenメソッドは無効なトークンが与えられた場合JWTDecodeExceptionをスローすること() {
        val invalidToken = "invalidToken"
        assertThrows<JWTDecodeException> {
            jwtProvider.verifyToken(invalidToken)
        }
    }

    @Test
    fun verifyTokenメソッドは期限切れのトークンが与えられた場合SignatureVerificationExceptionをスローすること() {
        val expiredToken = JWT.create()
            .withExpiresAt(Date(System.currentTimeMillis() - 1000))
            .sign(Algorithm.HMAC256("test"))
        assertThrows<SignatureVerificationException> {
            jwtProvider.verifyToken(expiredToken)
        }
    }

    @Test
    fun `X_AUTH_TOKENの値が「X-AUTH-TOKEN」であること`() {
        assertEquals("X-AUTH-TOKEN", X_AUTH_TOKEN)
    }

    @Test
    fun `CLAIM_ROLESの値が「roles」であること`() {
        assertEquals("roles", CLAIM_ROLES)
    }
}