package com.yutakomura.usecase.user.signup

import com.yutakomura.infrastructure.security.LoginUser
import com.yutakomura.usecase.core.OutputData

data class SignupOutputData(val loginUser: LoginUser) : OutputData
