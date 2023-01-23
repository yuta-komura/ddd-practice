package com.yutakomura

import com.yutakomura.entity.Role
import com.yutakomura.entity.User
import com.yutakomura.repository.RoleRepository
import com.yutakomura.repository.UserRepository
import com.yutakomura.security.*
import com.yutakomura.security.JWTProvider.Companion.X_AUTH_TOKEN
import com.yutakomura.security.SecurityConfiguration.Companion.ROLE_PREMIUM
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class Controller(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val jwtProvider: JWTProvider,
    private val passwordEncoder: PasswordEncoder,
    private val redisTemplate: StringRedisTemplate
) {

    @PostMapping(path = ["/api/signup"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun signup(
        @RequestBody body: EmailAndPasswordJsonRequest,
        @AuthenticationPrincipal loginUser: LoginUser?,
        httpServletResponse: HttpServletResponse
    ): UserAndRoleJsonResponse {
        val userNum = userRepository.selectNum()
        if (userNum == 0 || loginUser?.authorities?.first()?.authority.equals("ROLE_PREMIUM")) {
            val password = passwordEncoder.encode(body.password)
            val user = userRepository.insert(User(email = body.email, password = password)).entity
            val role = roleRepository.insert(Role(user.id!!)).entity
            val authToken = jwtProvider.createToken(LoginUser(user.id, listOf(SimpleGrantedAuthority(role.role))))
            httpServletResponse.setHeader(X_AUTH_TOKEN, authToken)
            return UserAndRoleJsonResponse(user, role)
        } else {
            throw AccessDeniedException("Access Denied")
        }
    }

    @PreAuthorize("hasRole('$ROLE_PREMIUM')") // ログイン時にDBから取得した権限に「PREMIUM」が含まれていればアクセス可能
    @PostMapping(path = ["/api/addRolePremium"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun addRolePremium(
        @RequestBody body: EmailJsonRequest,
        httpServletResponse: HttpServletResponse
    ): UserAndRoleJsonResponse {
        val user =
            userRepository.selectByEmail(body.email)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが存在しません。") }
        val role = roleRepository.update(Role(user.id!!, ROLE_PREMIUM)).entity
        return UserAndRoleJsonResponse(user, role)
    }

    @PreAuthorize("isAuthenticated()") // ログインしていればアクセス可能
    @GetMapping("/api/personal/user")
    fun personalUser(@AuthenticationPrincipal loginUser: LoginUser): User =
        userRepository.selectById(loginUser.id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが存在しません。") }

    @PreAuthorize("isAuthenticated()") // ログインしていればアクセス可能
    @GetMapping("/api/logout")
    fun logout(@AuthenticationPrincipal loginUser: LoginUser): String {
        val key = loginUser.id.toString()
        redisTemplate.delete(key)
        return "{\"message\": \"logout\"}"
    }
}