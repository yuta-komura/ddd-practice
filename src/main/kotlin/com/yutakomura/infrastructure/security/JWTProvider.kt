package com.yutakomura.infrastructure.security

import com.auth0.jwt.interfaces.DecodedJWT

interface JWTProvider {
    fun createToken(user: LoginUser): String
    fun getToken(request: jakarta.servlet.ServletRequest?): String?
    fun verifyToken(token: String): DecodedJWT
    fun retrieve(decodedJWT: DecodedJWT): LoginUser
}