package com.yutakomura.usecase.user.signup

import com.yutakomura.domain.role.Role
import com.yutakomura.domain.role.Value
import com.yutakomura.domain.user.Email
import com.yutakomura.domain.user.Password
import com.yutakomura.domain.user.Person
import com.yutakomura.infrastructure.security.LoginUser
import org.springframework.security.core.authority.SimpleGrantedAuthority

class SignupInteractor : SignupUseCase {
    override fun handle(inputData: SignupInputData): SignupOutputData {
        val person = Person(Email(inputData.email), Password(inputData.password))
        val addableUser = person.toAddableUser()
        val uniqueUser = addableUser.register()
        val role = Role(uniqueUser.id, Value())
        val registeredRole = role.register()
        val loginUser = LoginUser(uniqueUser.id.value, listOf(SimpleGrantedAuthority(registeredRole.value.value)))
        return SignupOutputData(loginUser)
    }
}