package com.yutakomura.domain.user

import com.yutakomura.infrastructure.Container
import org.seasar.doma.Domain
import org.springframework.security.crypto.password.PasswordEncoder

@Domain(valueType = String::class)
class EncodedPassword(val value: String) {
    companion object {
        private val passwordEncoder = Container.getBean(PasswordEncoder::class.java)
        fun from(password: Password): EncodedPassword {
            return EncodedPassword(passwordEncoder.encode(password.value))
        }
    }
}