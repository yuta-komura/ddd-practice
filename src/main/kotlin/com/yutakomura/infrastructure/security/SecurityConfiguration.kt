package com.yutakomura.infrastructure.security

import com.yutakomura.infrastructure.security.JWTProvider.Companion.X_AUTH_TOKEN
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
@EnableMethodSecurity
class SecurityConfiguration(
    private val jwtProvider: JWTProvider,
    private val authManagerBuilder: AuthenticationManagerBuilder,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf().disable() // Cookie/Sessionを利用しないため不要
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        val jsonAuthFilter = JsonRequestAuthenticationFilter(authManagerBuilder.orBuild)
        jsonAuthFilter.setRequiresAuthenticationRequestMatcher(
            AntPathRequestMatcher(
                "/api/login",
                "POST"
            )
        )
        jsonAuthFilter.setAuthenticationSuccessHandler { _, response, auth ->
            val principal: LoginUser = auth.principal as LoginUser
            val authToken = jwtProvider.createToken(principal)
            response.setHeader(X_AUTH_TOKEN, authToken)
            response.status = 200
        }
        http.addFilter(jsonAuthFilter)
        http.addFilterBefore(
            JWTTokenFilter(jwtProvider),
            JsonRequestAuthenticationFilter::class.java
        )
        return http.build()
    }
}
