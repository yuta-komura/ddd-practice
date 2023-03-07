package com.yutakomura.domain.user

import org.seasar.doma.Domain
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.security.crypto.password.PasswordEncoder

@Domain(valueType = String::class, factoryMethod = "from")
@Configurable(preConstruction = true)
class EncodedPassword private constructor(val value: String) {
    companion object {
        @Autowired
        private lateinit var passwordEncoder: PasswordEncoder
        fun from(password: Password): EncodedPassword {
            return EncodedPassword(passwordEncoder.encode(password.value))
        }
    }
}