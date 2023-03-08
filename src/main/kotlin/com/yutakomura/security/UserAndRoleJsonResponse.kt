package com.yutakomura.security

import com.fasterxml.jackson.annotation.JsonCreator
import com.yutakomura.entity.Role
import com.yutakomura.entity.User

data class UserAndRoleJsonResponse
@JsonCreator constructor(val user: User, val role: Role)
