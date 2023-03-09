package com.yutakomura.domain.user

import com.yutakomura.infrastructure.SpringDIContainer
import org.springframework.dao.DuplicateKeyException

class Person(
    val email: Email,
    val password: Password
) {

    private val userRepository: UserRepository =
        SpringDIContainer.getBean(UserRepository::class.java)

    fun toAddableUser(): AddableUser {
        userRepository.selectByEmail(email)
            .ifPresent { throw DuplicateKeyException("emailが重複しています。") }
        val encodedPassword = EncodedPassword.from(password)
        return AddableUser(email, encodedPassword)
    }
}