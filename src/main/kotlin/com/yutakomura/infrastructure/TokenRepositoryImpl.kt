package com.yutakomura.infrastructure

import com.yutakomura.config.security.Key
import com.yutakomura.config.security.Token
import com.yutakomura.config.security.TokenRepository
import com.yutakomura.config.security.Value
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate

class TokenRepositoryImpl : TokenRepository {

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

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