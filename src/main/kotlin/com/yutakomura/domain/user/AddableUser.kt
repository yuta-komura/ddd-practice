package com.yutakomura.domain.user

import com.yutakomura.infrastructure.Container

class AddableUser(
    val email: Email,
    val encodedPassword: EncodedPassword
) {

    private val userRepository = Container.getBean(UserRepository::class.java)

    fun register(): UniqueUser {
        userRepository.insert(email, encodedPassword)
        return userRepository.selectByEmail(email)
            .orElseThrow { RuntimeException("ユーザーアカウントが見つかりませんでした。") }
    }
}
