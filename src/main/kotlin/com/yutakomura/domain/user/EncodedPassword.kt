package com.yutakomura.domain.user

import org.seasar.doma.Domain
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Domain(valueType = String::class)
class EncodedPassword(val value: String) {
    companion object {
        private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
        fun from(password: Password): EncodedPassword {
            return EncodedPassword(passwordEncoder.encode(password.value))
        }
    }
}