package org.esc.serverportsmanager.io.handlers

import io.jsonwebtoken.security.SignatureException
import org.esc.serverportsmanager.exceptions.DoubleRecordException
import org.esc.serverportsmanager.exceptions.JwtAuthenticationException
import org.esc.serverportsmanager.exceptions.NotFoundException
import org.esc.serverportsmanager.io.BasicErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception): ResponseEntity<BasicErrorResponse> {
        val errorResponse = BasicErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            message = "${ex::class}; ${ex.message}",
        )

        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException): ResponseEntity<BasicErrorResponse> {
        val errorResponse = BasicErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            message = ex.message
        )

        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(DoubleRecordException::class)
    fun handleDoubleRecordException(ex: DoubleRecordException): ResponseEntity<BasicErrorResponse> {
        val errorResponse = BasicErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            message = ex.message
        )

        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(JwtAuthenticationException::class)
    fun handleJwtAuthenticationException(ex: JwtAuthenticationException): ResponseEntity<BasicErrorResponse> {
        val errorResponse = ex.message?.let {
            BasicErrorResponse(
                status = HttpStatus.UNAUTHORIZED.value(),
                message = it
            )
        }

        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(SignatureException::class)
    fun handleSignatureException(ex: SignatureException): ResponseEntity<BasicErrorResponse> {
        val errorResponse = ex.message?.let {
            BasicErrorResponse(
                status = HttpStatus.LOCKED.value(),
                message = "Jwt token verification failed. JWT validity cannot be asserted and should not be trusted."
            )
        }

        return ResponseEntity(errorResponse, HttpStatus.LOCKED)
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(ex: AuthorizationDeniedException): ResponseEntity<BasicErrorResponse> {
        val errorResponse = ex.message?.let {
            BasicErrorResponse(
                status = HttpStatus.FORBIDDEN.value(),
                message = "Access denied. You don't have permission to access this resource."
            )
        }

        return ResponseEntity(errorResponse, HttpStatus.FORBIDDEN)
    }
}