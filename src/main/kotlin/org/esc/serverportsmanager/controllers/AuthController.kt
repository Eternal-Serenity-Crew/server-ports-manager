package org.esc.serverportsmanager.controllers

import org.esc.serverportsmanager.dto.auth.LoginUserDto
import org.esc.serverportsmanager.dto.auth.RegisterUserDto
import org.esc.serverportsmanager.dto.auth.UpdateUserTokensDto
import org.esc.serverportsmanager.dto.toHttpResponse
import org.esc.serverportsmanager.services.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val authService: AuthService) {
    @PostMapping("/register")
    fun register(@RequestBody data: RegisterUserDto) = authService.register(data).toHttpResponse()

    @PostMapping("/login")
    fun login(@RequestBody data: LoginUserDto) = authService.login(data).toHttpResponse()

    @PostMapping("/updateTokens")
    fun updateTokens(@RequestBody data: UpdateUserTokensDto) = authService.updateTokens(data).toHttpResponse()
}