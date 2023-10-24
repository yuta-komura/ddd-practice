package com.yutakomura.controller

import com.yutakomura.controller.response.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
class CustomErrorController(
    errorAttributes: ErrorAttributes
) : AbstractErrorController(errorAttributes) {

    @RequestMapping("/error")
    @ResponseBody
    fun handleError(request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errorAttributes = getErrorAttributes(
            request, ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.STACK_TRACE
            )
        )
        val errorResponse = ErrorResponse.fromMap(errorAttributes)
        return ResponseEntity<ErrorResponse>(
            errorResponse,
            HttpStatus.valueOf(errorResponse.status)
        )
    }
}
