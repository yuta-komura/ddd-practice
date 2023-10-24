package com.yutakomura.usecase.user.signup

import com.yutakomura.usecase.core.InputData

data class SignupInputData(val email: String, val password: String) : InputData<SignupOutputData>