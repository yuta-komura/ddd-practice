package com.yutakomura.usecase.user.signup

import com.yutakomura.infrastructure.security.LoginUser

data class SignupOutputData(val loginUser: LoginUser)