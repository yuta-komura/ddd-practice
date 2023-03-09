//package com.yutakomura.controller.user
//
//import com.yutakomura.controller.request.EmailAndPasswordJsonRequest
//import com.yutakomura.controller.request.EmailJsonRequest
//import com.yutakomura.controller.response.UserAndRoleJsonResponse
//import com.yutakomura.domain.role.RoleRepository
//import com.yutakomura.domain.user.UserRepository
//import com.yutakomura.entity.Role
//import com.yutakomura.entity.User
//import com.yutakomura.infrastructure.Message
//import com.yutakomura.infrastructure.security.JWTProvider
//import com.yutakomura.infrastructure.security.JWTProviderImpl.Companion.X_AUTH_TOKEN
//import com.yutakomura.infrastructure.security.LoginUser
//import com.yutakomura.security.*
//import com.yutakomura.security.SecurityConfiguration.Companion.ROLE_PREMIUM
//import com.yutakomura.usecase.user.signup.SignupInputData
//import com.yutakomura.usecase.user.signup.SignupUseCase
//import jakarta.servlet.http.HttpServletResponse
//import org.springframework.context.MessageSource
//import org.springframework.data.redis.core.StringRedisTemplate
//import org.springframework.http.HttpStatus
//import org.springframework.http.MediaType
//import org.springframework.security.access.prepost.PreAuthorize
//import org.springframework.security.core.annotation.AuthenticationPrincipal
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.PostMapping
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RestController
//import org.springframework.web.server.ResponseStatusException
//import java.util.*
//
//@RestController
//class Controller(
//    private val userRepository: UserRepository,
//    private val roleRepository: RoleRepository,
//    private val passwordEncoder: PasswordEncoder,
//    private val redisTemplate: StringRedisTemplate,
//    private val jwtProvider: JWTProvider,
//    private val signup: SignupUseCase,
//    private val messageSource: MessageSource
//) {
//
//    @PostMapping(path = ["/api/signup"], produces = [MediaType.APPLICATION_JSON_VALUE])
//    fun signup(
//        @RequestBody body: EmailAndPasswordJsonRequest,
//        httpServletResponse: HttpServletResponse
//    ): String {
//        val inputData = SignupInputData(body.email, body.password)
//        val outputData = signup.handle(inputData)
//        val loginUser = outputData.loginUser
//        val authToken = jwtProvider.createToken(outputData.loginUser)
//        httpServletResponse.setHeader(X_AUTH_TOKEN, authToken)
//        return Message.of("message1", arrayOf(loginUser.id.toString()))
//    }
//
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
//
//    @PreAuthorize("isAuthenticated()") // ログインしていればアクセス可能
//    @GetMapping("/api/logout")
//    fun logout(@AuthenticationPrincipal loginUser: LoginUser): String {
//        val key = loginUser.id.toString()
//        redisTemplate.delete(key)
//        return "{\"message\": \"logout\"}"
//    }
//}