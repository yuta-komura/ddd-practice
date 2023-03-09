package com.yutakomura.infrastructure

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    companion object : Log()

    @ExceptionHandler(Exception::class)
    @ResponseBody
    fun handleException(request: HttpServletRequest, e: Exception): ResponseEntity<ErrorResponse> {
        log.error(e.message)
        e.printStackTrace()
        val status = getStatus(request)
        return ResponseEntity<ErrorResponse>(
            ErrorResponse(e.message, e.stackTraceToString()),
            status
        )
    }

    private fun getStatus(request: HttpServletRequest): HttpStatus {
        val statusCode: Int = request.getAttribute("javax.servlet.error.status_code") as Int?
            ?: return HttpStatus.INTERNAL_SERVER_ERROR
        return HttpStatus.valueOf(statusCode)
    }
}