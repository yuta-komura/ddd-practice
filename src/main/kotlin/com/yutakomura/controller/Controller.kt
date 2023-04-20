package com.yutakomura.controller.user

import com.yutakomura.controller.request.EmailAndPasswordJsonRequest
import com.yutakomura.domain.role.RoleRepository
import com.yutakomura.domain.user.UserRepository
import com.yutakomura.infrastructure.Message
import com.yutakomura.infrastructure.security.JWTProvider
import com.yutakomura.infrastructure.security.JWTProvider.Companion.X_AUTH_TOKEN
import com.yutakomura.usecase.user.signup.SignupInputData
import com.yutakomura.usecase.user.signup.SignupUseCase
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
//    private val redisTemplate: StringRedisTemplate,
    private val jwtProvider: JWTProvider,
    private val signup: SignupUseCase,
    private val messageSource: MessageSource
) {

    @PostMapping(path = ["/api/signup"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun signup(
        @RequestBody body: EmailAndPasswordJsonRequest,
        httpServletResponse: HttpServletResponse
    ): String {
        val inputData = SignupInputData(body.email, body.password)
        val outputData = signup.handle(inputData)
        val loginUser = outputData.loginUser
        val authToken = jwtProvider.createToken(outputData.loginUser)
        httpServletResponse.setHeader(X_AUTH_TOKEN, authToken)
        return Message.of("message1", arrayOf(loginUser.id.toString()))
    }

//    @PreAuthorize("hasRole('$ROLE_PREMIUM')") // ログイン時にDBから取得した権限に「PREMIUM」が含まれていればアクセス可能
//    @PostMapping(path = ["/api/addRolePremium"], produces = [MediaType.APPLICATION_JSON_VALUE])
//    fun addRolePremium(
//        @RequestBody body: EmailJsonRequest,
//        httpServletResponse: HttpServletResponse
//    ): UserAndRoleJsonResponse {
//        val user =
//            userRepository.selectByEmail(body.email)
//                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが存在しません。") }
//        val role = roleRepository.update(Role(user.id, ROLE_PREMIUM)).entity
//        return UserAndRoleJsonResponse(user, role)
//    }
//
//    @PreAuthorize("isAuthenticated()") // ログインしていればアクセス可能
//    @GetMapping("/api/personal/user")
//    fun personalUser(@AuthenticationPrincipal loginUser: LoginUser): User =
//        userRepository.selectById(loginUser.id)
//            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが存在しません。") }

//    @PreAuthorize("isAuthenticated()") // ログインしていればアクセス可能
//    @GetMapping("/api/logout")
//    fun logout(@AuthenticationPrincipal loginUser: LoginUser): String {
//        val key = loginUser.id.toString()
//        redisTemplate.delete(key)
//        return "{\"message\": \"logout\"}"
//    }
}