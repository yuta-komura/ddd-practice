package com.yutakomura.infrastructure.security

import com.auth0.jwt.interfaces.DecodedJWT

interface JWTProvider {
    fun createToken(loginUser: LoginUser): String
    fun tokenValueFrom(request: jakarta.servlet.ServletRequest): String
    fun verifyToken(token: String): DecodedJWT
    fun retrieve(decodedJWT: DecodedJWT): LoginUser

    companion object {
        const val X_AUTH_TOKEN = "X-AUTH-TOKEN"
        const val CLAIM_ROLES = "roles"
    }
}