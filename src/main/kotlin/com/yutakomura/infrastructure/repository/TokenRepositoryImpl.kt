package com.yutakomura.infrastructure.repository

import com.yutakomura.infrastructure.security.Key
import com.yutakomura.infrastructure.security.Token
import com.yutakomura.infrastructure.security.TokenRepository
import com.yutakomura.infrastructure.security.Value
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class TokenRepositoryImpl : TokenRepository {

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    override fun selectByKey(key: Key): Token? {
        val token = redisTemplate.opsForValue()[key.value] ?: return null
        return Token(Key(key.value), Value(token))
    }

    override fun insert(token: Token): Int {
        redisTemplate.opsForValue()[token.key.value] = token.value.value
        return 1
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