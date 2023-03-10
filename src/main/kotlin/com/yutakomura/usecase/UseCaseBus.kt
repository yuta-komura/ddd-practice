package com.yutakomura.usecase

import com.yutakomura.infrastructure.Container
import com.yutakomura.usecase.core.InputData
import com.yutakomura.usecase.core.OutputData
import com.yutakomura.usecase.core.UseCase
import org.springframework.stereotype.Component

@Component
class UseCaseBus {
    @Suppress("UNCHECKED_CAST")
    fun <TInputData : InputData<*>, TOutputData : OutputData> handle(inputData: TInputData): TOutputData {
        val clazzInputData = inputData.javaClass
        val packageName = clazzInputData.`package`.name
        val suffix = clazzInputData.simpleName.replace("InputData", "")
        val clazzUsecase = Class.forName("${packageName}.${suffix}UseCase")
        val usecase =
            Container.getBean(clazzUsecase) as UseCase<TInputData, TOutputData>
        return usecase.handle(inputData)
    }
}
