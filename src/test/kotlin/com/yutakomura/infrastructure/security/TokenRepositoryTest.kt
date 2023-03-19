package com.yutakomura.infrastructure.security

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate

@SpringBootTest
@AutoConfigureMockMvc
class TokenRepositoryTest {

    @Autowired
    lateinit var tokenRepository: TokenRepository

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    private lateinit var token: Token

    private lateinit var key: Key

    @BeforeEach
    fun beforeEach() {
        key = Key("test-key")
        token = Token(key, Value("test-value"))
        redisTemplate.opsForValue().set(key.value, token.value.value)
    }

    @Test
    fun selectByKeyTest() {
        val resultToken = tokenRepository.selectByKey(key)
        assertNotNull(resultToken)
        assertEquals(token, resultToken)
    }

    @Test
    fun selectByKeyWithNonexistentKeyTest() {
        val nonExistentKey = Key("non-existent-key")
        val resultToken = tokenRepository.selectByKey(nonExistentKey)
        assertNull(resultToken)
    }

    @Test
    fun insertTest() {
        val newToken = Token(Key("new-key"), Value("new-value"))
        val result = tokenRepository.insert(newToken)
        assertEquals(1, result)
        assertEquals(newToken, tokenRepository.selectByKey(newToken.key))
    }

    @Test
    fun deleteByKeysTest() {
        val keysToDelete = listOf(key)
        val result = tokenRepository.deleteByKeys(keysToDelete)
        assertEquals(1, result)
        assertNull(tokenRepository.selectByKey(key))
    }

    @Test
    fun deleteByKeysWithNonexistentKeyTest() {
        val nonExistentKey = Key("non-existent-key")
        val keysToDelete = listOf(nonExistentKey)
        val result = tokenRepository.deleteByKeys(keysToDelete)
        assertEquals(1, result)
    }
}