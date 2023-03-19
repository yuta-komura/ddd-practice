package com.yutakomura.infrastructure.security

interface TokenRepository {

    fun selectByKey(key: Key): Token?

    fun insert(token: Token): Int

    fun deleteByKeys(keys: List<Key>): Int
}
