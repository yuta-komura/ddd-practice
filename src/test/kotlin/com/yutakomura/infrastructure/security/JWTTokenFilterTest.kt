package com.yutakomura.infrastructure.security


import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension::class)
class JWTTokenFilterTest {

    private lateinit var jwtTokenFilter: JWTTokenFilter

    @MockBean
    private lateinit var jwtProvider: JWTProvider

    @MockBean
    private lateinit var tokenRepository: TokenRepository

    private lateinit var request: HttpServletRequest

    private lateinit var response: HttpServletResponse

    private lateinit var filterChain: FilterChain

    private lateinit var token: Token

    @BeforeEach
    fun beforeEach() {
        jwtTokenFilter = JWTTokenFilter(jwtProvider)
        request = mock(HttpServletRequest::class.java)
        response = mock(HttpServletResponse::class.java)
        filterChain = mock(FilterChain::class.java)
        token = mock(Token::class.java)
    }

    @Test
    fun ログインエンドポイントがフィルタをバイパスする() {
        `when`(request.requestURI).thenReturn("/api/login")

        jwtTokenFilter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(request, response)
        verifyNoInteractions(jwtProvider, tokenRepository)
    }

    @Test
    fun エラーエンドポイントがフィルタをバイパスする() {
        `when`(request.requestURI).thenReturn("/error")

        jwtTokenFilter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(request, response)
        verifyNoInteractions(jwtProvider, tokenRepository)
    }

    @Test
    fun JWTが有効な場合フィルタが正常に実行される() {
        val token = "valid_token"
        val decodedJWT = mock(DecodedJWT::class.java)
        val loginUser = mock(LoginUser::class.java)
        val key = Key(loginUser.id.toString())
        val tokenRedis = Token(key, Value(token))

        `when`(request.requestURI).thenReturn("/api/some_endpoint")
        `when`(jwtProvider.tokenValueFrom(request)).thenReturn(token)
        `when`(jwtProvider.verifyToken(token)).thenReturn(decodedJWT)
        `when`(jwtProvider.retrieve(decodedJWT)).thenReturn(loginUser)
        `when`(tokenRepository.selectBy(key)).thenReturn(tokenRedis)

        jwtTokenFilter.doFilter(request, response, filterChain)

        verify(jwtProvider).tokenValueFrom(request)
        verify(jwtProvider).verifyToken(token)
        verify(jwtProvider).retrieve(decodedJWT)
        verify(tokenRepository).selectBy(key)
        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun JWTが無効な場合JWTVerificationExceptionがスローされる() {
        val token = "invalid_token"
        val exceptionMessage = "トークンが無効です。"

        `when`(request.requestURI).thenReturn("/api/some_endpoint")
        `when`(jwtProvider.tokenValueFrom(request)).thenReturn(token)
        `when`(jwtProvider.verifyToken(token)).thenThrow(JWTVerificationException(exceptionMessage))

        assertThrows<JWTVerificationException> {
            jwtTokenFilter.doFilter(request, response, filterChain)
        }.apply {
            assertThat(this.message).isEqualTo(exceptionMessage)
        }

        verify(jwtProvider).tokenValueFrom(request)
        verify(jwtProvider).verifyToken(token)
        verifyNoMoreInteractions(jwtProvider)
        verifyNoInteractions(tokenRepository, filterChain)
    }

    @Test
    fun Redisに格納されたトークンとJWTトークンが一致しない場合JWTVerificationExceptionがスローされる() {
        val token = "valid_token"
        val anotherToken = "another_token"
        val decodedJWT = mock(DecodedJWT::class.java)
        val loginUser = mock(LoginUser::class.java)
        val key = Key(loginUser.id.toString())
        val tokenRedis = Token(key, Value(anotherToken))

        `when`(request.requestURI).thenReturn("/api/some_endpoint")
        `when`(jwtProvider.tokenValueFrom(request)).thenReturn(token)
        `when`(jwtProvider.verifyToken(token)).thenReturn(decodedJWT)
        `when`(jwtProvider.retrieve(decodedJWT)).thenReturn(loginUser)
        `when`(tokenRepository.selectBy(key)).thenReturn(tokenRedis)

        assertThrows<JWTVerificationException> {
            jwtTokenFilter.doFilter(request, response, filterChain)
        }.apply {
            assertThat(this.message).isEqualTo("トークンが無効です。")
        }

        verify(jwtProvider).tokenValueFrom(request)
        verify(jwtProvider).verifyToken(token)
        verify(jwtProvider).retrieve(decodedJWT)
        verify(tokenRepository).selectBy(key)
        verifyNoInteractions(filterChain)
    }

    @Test
    fun Redisに格納されたトークン取得できない場合JWTVerificationExceptionがスローされる1() {
        val token = "valid_token"
        val decodedJWT = mock(DecodedJWT::class.java)
        val loginUser = mock(LoginUser::class.java)
        val key = Key(loginUser.id.toString())

        `when`(request.requestURI).thenReturn("/api/some_endpoint")
        `when`(jwtProvider.tokenValueFrom(request)).thenReturn(token)
        `when`(jwtProvider.verifyToken(token)).thenReturn(decodedJWT)
        `when`(jwtProvider.retrieve(decodedJWT)).thenReturn(loginUser)
        `when`(tokenRepository.selectBy(key)).thenReturn(null)

        assertThrows<JWTVerificationException> {
            jwtTokenFilter.doFilter(request, response, filterChain)
        }.apply {
            assertThat(this.message).isEqualTo("トークンが無効です。")
        }

        verify(jwtProvider).tokenValueFrom(request)
        verify(jwtProvider).verifyToken(token)
        verify(jwtProvider).retrieve(decodedJWT)
        verify(tokenRepository).selectBy(key)
        verifyNoInteractions(filterChain)
    }
}