package com.yutakomura.infrastructure.security

import com.yutakomura.infrastructure.Container
import org.springframework.data.redis.core.StringRedisTemplate

class TokenRepositoryImpl : TokenRepository {

    private val redisTemplate = Container.getBean(StringRedisTemplate::class.java)

    override fun selectByKey(key: Key): Token {
        val token = redisTemplate.opsForValue()[key.value]
        return Token(Key(key.value), Value(token))
    }

    override fun insert(token: Token): Token {
        redisTemplate.opsForValue()[token.key.value] = token.value.value!!
        return token
    }

    override fun deleteByKeys(keys: List<Key>): Int {
        var num = 0
        keys.forEach {
            redisTemplate.delete(it.value)
            num++
        }
        return num
    }
}