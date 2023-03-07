package com.yutakomura.domain.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable

@Configurable(preConstruction = true)
class AddableUser(
    val email: Email,
    val encodedPassword: EncodedPassword
) {
    @Autowired
    private lateinit var userRepository: UserRepository

    fun register(): UniqueUser {
        return userRepository.insert(null, email, encodedPassword).entity
    }
}
