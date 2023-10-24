package com.yutakomura.domain.role

import com.yutakomura.domain.user.Id
import com.yutakomura.infrastructure.Container

data class Role(
    val userid: Id,
    val value: Value
) {

    private val roleRepository = Container.getBean(RoleRepository::class.java)

    fun give(): GivenRole {
        roleRepository.insert(userid, value)
        return GivenRole(userid, value)
    }

}