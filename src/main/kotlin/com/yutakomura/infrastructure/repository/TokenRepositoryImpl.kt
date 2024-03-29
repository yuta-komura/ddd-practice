package com.yutakomura.infrastructure.repository

import com.yutakomura.infrastructure.security.Key
import com.yutakomura.infrastructure.security.Token
import com.yutakomura.infrastructure.security.TokenRepository
import com.yutakomura.infrastructure.security.Value
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import org.springframework.stereotype.Repository

@Repository
class TokenRepositoryImpl : TokenRepository {

    @org.springframework.beans.factory.annotation.Value("\${spring.redis.host}")
    private lateinit var host: String

    @org.springframework.beans.factory.annotation.Value("\${spring.redis.password}")
    private lateinit var password: String

    override fun selectBy(key: Key): Token? {
        return client().connect().use { connection ->
            val value = connection.sync().get(key.value)
            value?.let { Token(Key(key.value), Value(it)) }
        }
    }

    override fun insert(token: Token): Int {
        return client().connect().use { connection ->
            val result = connection.sync().set(token.key.value, token.value.value)
            if (result == "OK") 1 else 0
        }
    }

    override fun insert(tokens: List<Token>): Int {
        return client().connect().use { connection ->
            val commands = connection.async()
            commands.multi()
            tokens.forEach { token ->
                commands.set(token.key.value, token.value.value)
            }
            commands.exec().toCompletableFuture().join()
            tokens.size
        }
    }

    override fun deleteBy(key: Key): Int {
        return client().connect().use { connection ->
            val result = connection.sync().del(key.value)
            if (result > 0) 1 else 0
        }
    }

    override fun deleteBy(keys: List<Key>): Int {
        return client().connect().use { connection ->
            val commands = connection.async()
            commands.multi()
            keys.forEach { key ->
                commands.del(key.value)
            }
            commands.exec().toCompletableFuture().join()
            keys.size
        }
    }

    private fun client(): RedisClient {
        val redisURI = RedisURI.builder()
            .withHost(host)
            .withPassword(password.toCharArray())
            .build()
        return RedisClient.create(redisURI)
    }
}
