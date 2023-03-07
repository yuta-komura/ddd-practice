package com.yutakomura.infrastructure.security

import com.yutakomura.domain.user.UserRepository
import com.yutakomura.repository.RoleRepository
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
class JsonRequestAuthenticationProvider(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val email = authentication.principal as String
        val password = authentication.credentials as String
        val user = userRepository.selectByEmail(email).orElseThrow { BadCredentialsException("ユーザーアカウントが見つかりませんでした。") }
        if (!passwordEncoder.matches(password, user.password)) {
            throw BadCredentialsException("パスワードが正しくありません。")
        }
        val roles = roleRepository.selectByUserId(user.id)
        if (roles.isEmpty()) {
            throw BadCredentialsException("権限が見つかりませんでした。")
        }
        val loginUser = LoginUser(user.id, roles.map { SimpleGrantedAuthority(it.role) })
        return UsernamePasswordAuthenticationToken(loginUser, null, loginUser.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}
