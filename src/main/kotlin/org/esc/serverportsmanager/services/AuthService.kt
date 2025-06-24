package org.esc.serverportsmanager.services

import org.esc.serverportsmanager.dto.auth.LoginUserDto
import org.esc.serverportsmanager.dto.auth.RegisterUserDto
import org.esc.serverportsmanager.dto.auth.UpdateUserTokensDto
import org.esc.serverportsmanager.dto.jwt.CreateJwtToken
import org.esc.serverportsmanager.dto.jwt.UserTokensDto
import org.esc.serverportsmanager.dto.users.CreateUserDto
import org.esc.serverportsmanager.exceptions.JwtAuthenticationException
import org.esc.serverportsmanager.utils.JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val usersService: UsersService,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder,
) {
    fun register(data: RegisterUserDto): UserTokensDto {
        return usersService.create(CreateUserDto(data.username, data.email, data.password, data.role)).let {
            jwtUtil.removeOldRefreshTokenByUUID(data.uuid)
            UserTokensDto(
                jwtUtil.generateAccessToken(CreateJwtToken(it, data.uuid)),
                jwtUtil.generateRefreshToken(CreateJwtToken(it, data.uuid))
            )
        }
    }

    fun login(data: LoginUserDto): UserTokensDto {
        return usersService.getByEmail(data.email).let {
            jwtUtil.removeOldRefreshTokenByUUID(data.uuid)
            if (passwordEncoder.matches(data.password, it!!.password)) {
                UserTokensDto(
                    jwtUtil.generateAccessToken(CreateJwtToken(it, data.uuid)),
                    jwtUtil.generateRefreshToken(CreateJwtToken(it, data.uuid))
                )
            } else throw JwtAuthenticationException("Wrong email or password")
        }
    }

    fun updateTokens(data: UpdateUserTokensDto): UserTokensDto {
        jwtUtil.verifyToken(data.accessToken, throwTimeLimit = false)
        jwtUtil.verifyToken(data.refreshToken, data.uuid)
        jwtUtil.removeOldRefreshTokenByUUID(data.uuid)

        return jwtUtil.getUserFromToken(data.refreshToken)?.run {
            UserTokensDto(
                jwtUtil.generateAccessToken(CreateJwtToken(this, data.uuid)),
                jwtUtil.generateRefreshToken(CreateJwtToken(this, data.uuid))
            )
        } ?: throw JwtAuthenticationException("Invalid refresh token")
    }
}