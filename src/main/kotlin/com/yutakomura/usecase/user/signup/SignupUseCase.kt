package com.yutakomura.usecase.user.signup

interface SignupUseCase {
    fun handle(inputData: SignupInputData): SignupOutputData
}