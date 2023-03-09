package com.yutakomura.domain.user

import com.yutakomura.infrastructure.SpringDIContainer
import org.springframework.beans.factory.annotation.Configurable

@Configurable(preConstruction = true)
class AddableUser(
    val email: Email,
    val encodedPassword: EncodedPassword
) {

    private val userRepository = SpringDIContainer.getBean(UserRepository::class.java)

    fun register(): UniqueUser {
        userRepository.insert(email, encodedPassword)
        return userRepository.selectByEmail(email)
            .orElseThrow { RuntimeException("ユーザーアカウントが見つかりませんでした。") }
    }
}
