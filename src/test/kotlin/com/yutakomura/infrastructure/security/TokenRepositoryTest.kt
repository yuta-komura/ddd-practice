package com.yutakomura.infrastructure.security


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension::class)
class TokenRepositoryTest {

    @Autowired
    private lateinit var tokenRepository: TokenRepository

    @BeforeEach
    fun setUp() {
        // テストデータをクリア
        val keys = listOf(Key("testKey1"), Key("testKey2"))
        tokenRepository.deleteBy(keys)
    }

    @Test
    fun insertメソッドで1つのTokenを登録できる() {
        val token = Token(Key("testKey1"), Value("testValue1"))
        val actual = tokenRepository.insert(token)
        assertEquals(1, actual)
    }

    @Test
    fun insertメソッドで複数のTokenを登録できる() {
        val token1 = Token(Key("testKey1"), Value("testValue1"))
        val token2 = Token(Key("testKey2"), Value("testValue2"))
        val tokens = listOf(token1, token2)
        val actual = tokenRepository.insert(tokens)
        assertEquals(2, actual)
    }

    @Test
    fun selectByメソッドで存在するTokenを取得できる() {
        val key = Key("testKey1")
        val value = Value("testValue1")
        val expected = Token(key, value)
        tokenRepository.insert(expected)
        val actual = tokenRepository.selectBy(key)
        assertEquals(expected, actual)
    }

    @Test
    fun selectByメソッドで存在しないTokenを取得しnullが返る() {
        val key = Key("testKey1")
        val actual = tokenRepository.selectBy(key)
        assertEquals(null, actual)
    }

    @Test
    fun deleteByメソッドで存在するTokenを削除できる() {
        val key = Key("testKey1")
        tokenRepository.insert(Token(key, Value("testValue1")))
        val actual = tokenRepository.deleteBy(key)
        assertEquals(1, actual)
    }

    @Test
    fun deleteByメソッドで存在しないTokenを削除してもエラーにならない() {
        val key = Key("testKey1")
        val actual = tokenRepository.deleteBy(key)
        assertEquals(0, actual)
    }
}
