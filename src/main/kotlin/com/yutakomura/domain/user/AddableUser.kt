package com.yutakomura.domain.user

import com.yutakomura.infrastructure.Container

class AddableUser(
    val email: Email,
    val encodedPassword: EncodedPassword
) {

    private val userRepository = Container.getBean(UserRepository::class.java)

    fun register(): UniqueUser {
        userRepository.insert(email, encodedPassword)
        val uniqueUser = userRepository.selectByEmail(email)
        uniqueUser ?: throw RuntimeException("ユーザーアカウントが見つかりませんでした。")
        return uniqueUser
    }
}
