package com.yutakomura.domain.user

import com.yutakomura.infrastructure.Container
import org.springframework.dao.DuplicateKeyException

class Person(
    val email: Email,
    val password: Password
) {

    private val userRepository: UserRepository =
        Container.getBean(UserRepository::class.java)

    fun toAddableUser(): AddableUser {
        userRepository.selectByEmail(email)
            .ifPresent { throw DuplicateKeyException("emailが重複しています。") }
        val encodedPassword = EncodedPassword.from(password)
        return AddableUser(email, encodedPassword)
    }
}