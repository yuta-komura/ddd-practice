package com.yutakomura.controller.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

class ErrorResponse private constructor(
    val timestamp: Date,
    val status: Int,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val error: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val trace: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val message: String?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val path: String?
) {
    companion object {
        fun fromMap(errorAttributes: Map<String, Any>): ErrorResponse {
            val timestamp = errorAttributes["timestamp"] as Date
            val status = errorAttributes["status"] as Int
            val error = errorAttributes["error"] as String?
            val trace = errorAttributes["trace"] as String?
            val message = errorAttributes["message"] as String?
            val path = errorAttributes["path"] as String?
            return ErrorResponse(
                timestamp = timestamp,
                status = status,
                error = error,
                trace = trace,
                message = message,
                path = path
            )
        }
    }
}