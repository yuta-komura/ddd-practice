package com.yutakomura.infrastructure.security

interface TokenRepository {

    fun selectByKey(key: Key): Token

    fun insert(token: Token): Token

    fun deleteByKeys(keys: List<Key>): Int
}
