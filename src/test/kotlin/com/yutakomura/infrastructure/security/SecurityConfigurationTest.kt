package com.yutakomura.infrastructure.security

import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.ObjectMapper
import com.yutakomura.domain.user.Email
import com.yutakomura.domain.user.EncodedPassword
import com.yutakomura.domain.user.Password
import com.yutakomura.domain.user.UserRepository
import com.yutakomura.infrastructure.security.JWTProvider.Companion.X_AUTH_TOKEN
import com.yutakomura.usecase.user.signup.SignupUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.nio.file.Paths

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension::class)
class SecurityConfigurationTest {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val testDirPath = "src/test/kotlin/com/yutakomura/infrastructure/security"

    @Autowired
    private lateinit var jwtProvider: JWTProvider

    @Autowired
    private lateinit var tokenRepository: TokenRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var signup: SignupUseCase

    @BeforeEach
    fun setup() {
        val email = Email("aaa@gmail.com")
        userRepository.deleteByEmail(email)

        val password = Password("aaaa")
        val encodedPassword = EncodedPassword.from(password)
        userRepository.insert(email, encodedPassword)
//        every { passwordEncoderMock.encode(password) } returns "{bcrypt}\$2a\$10\$275XYyZ6IOE2p0Jxw4lY7.UeZo9oZRJJS9qtD/DJ3pZa39gBeZ4Y."
    }

    @Test
    fun `ログインが成功した場合X-Auth-Tokenを返却し、トークンがRedisに保存されること`() {
        val json = objectMapper.readTree(
            Paths.get("${testDirPath}/body1.json").toFile()
        )
        val response = mockMvc.post("/api/login") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andReturn().response
        val actualStatus = response.status
        val token = response.getHeader(X_AUTH_TOKEN)
        assertEquals(200, actualStatus)
        assertNotNull(token)
        val decodedJWT: DecodedJWT = jwtProvider.verifyToken(token!!)
        val loginUser: LoginUser = jwtProvider.retrieve(decodedJWT)
        val redisRegisteredToken = tokenRepository.selectBy(Key(loginUser.id.toString()))
        assertNotNull(redisRegisteredToken)
        assertEquals(redisRegisteredToken!!.value.value, token)
    }

    @Test
    fun `ログインが失敗した場合X-Auth-Tokenが返却されないこと`() {
        val json = objectMapper.readTree(
            Paths.get("${testDirPath}/body2.json").toFile()
        )
        val response = mockMvc.post("/api/login") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andReturn().response
        val actualStatus = response.status
        val token = response.getHeader(X_AUTH_TOKEN)
        assertEquals(401, actualStatus)
        assertNull(token)
    }
}