package com.yutakomura.infrastructure

import lombok.Value

@Value
data class ErrorResponse(
    val message: String?,
    val stackTrace: String?
)