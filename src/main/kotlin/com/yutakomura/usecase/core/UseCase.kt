package com.yutakomura.usecase.core

interface UseCase<TInputData : InputData<*>, TOutputData : OutputData> {
    fun handle(inputData: TInputData): TOutputData
}