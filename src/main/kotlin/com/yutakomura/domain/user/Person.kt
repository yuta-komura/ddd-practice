package com.yutakomura.domain.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.dao.DuplicateKeyException

@Configurable(preConstruction = true)
data class Person(
    val email: Email,
    val password: Password
) {

    @Autowired
    private lateinit var userRepository: UserRepository

    fun toAddableUser(): AddableUser {
        val duplicateUser = userRepository.selectByEmail(email).orElse(null)
        duplicateUser ?: throw DuplicateKeyException("emailが重複しています。")
        val encodedPassword = EncodedPassword.from(password)
        return AddableUser(email, encodedPassword)
    }
}