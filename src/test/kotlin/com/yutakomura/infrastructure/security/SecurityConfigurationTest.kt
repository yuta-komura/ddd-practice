package com.yutakomura.infrastructure.security

import com.auth0.jwt.interfaces.DecodedJWT
import com.fasterxml.jackson.databind.ObjectMapper
import com.yutakomura.domain.role.RoleRepository
import com.yutakomura.domain.role.Value
import com.yutakomura.domain.user.Email
import com.yutakomura.domain.user.EncodedPassword
import com.yutakomura.domain.user.UniqueUser
import com.yutakomura.domain.user.UserRepository
import com.yutakomura.infrastructure.security.JWTProvider.Companion.X_AUTH_TOKEN
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.factory.PasswordEncoderFactories
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
    private lateinit var roleRepository: RoleRepository

    @BeforeEach
    fun setup() {
        val json = objectMapper.readTree(
            Paths.get("${testDirPath}/body1.json").toFile()
        )
        val email = Email(json["email"].textValue())
        userRepository.deleteByEmail(email)
        val encodedPassword = EncodedPassword(
            PasswordEncoderFactories.createDelegatingPasswordEncoder()
                .encode(json["password"].textValue())
        )
        userRepository.insert(email, encodedPassword)
        val uniqueUser: UniqueUser? = userRepository.selectByEmail(email)
        roleRepository.insert(uniqueUser!!.id, Value("free"))
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