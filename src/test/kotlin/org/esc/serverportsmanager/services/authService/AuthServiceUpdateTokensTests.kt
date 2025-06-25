package org.esc.serverportsmanager.services.authService

import org.assertj.core.api.Assertions.assertThat
import org.esc.serverportsmanager.dto.auth.UpdateUserTokensDto
import org.esc.serverportsmanager.dto.jwt.UserTokensDto
import org.esc.serverportsmanager.entities.Users
import org.esc.serverportsmanager.entities.enums.Roles
import org.esc.serverportsmanager.exceptions.JwtAuthenticationException
import org.esc.serverportsmanager.repositories.JwtTokensRepository
import org.esc.serverportsmanager.services.AuthService
import org.esc.serverportsmanager.services.UserData.TEST_EMAIL
import org.esc.serverportsmanager.services.UserData.TEST_PASSWORD
import org.esc.serverportsmanager.services.UserData.TEST_USERNAME
import org.esc.serverportsmanager.services.UsersService
import org.esc.serverportsmanager.utils.JwtUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class AuthServiceUpdateTokensTests {
    @Mock
    private lateinit var userService: UsersService
    @Mock
    private lateinit var jwtUtil: JwtUtil
    @Mock
    private lateinit var passwordEncoder: PasswordEncoder
    @Mock
    private lateinit var jwtTokensRepository: JwtTokensRepository
    @InjectMocks
    private lateinit var authService: AuthService

    private fun createTestUser(
        id: Long = 1L,
        email: String = TEST_EMAIL,
        username: String = TEST_USERNAME
    ): Users {
        return Users(
            id = id,
            email = email,
            username = username,
            password = TEST_PASSWORD,
            role = Roles.USER
        )
    }

    private val dummyUser = createTestUser()
    private val userUUID = UUID.randomUUID()
    private val accessToken = "accessToken"
    private val refreshToken = "refreshToken"

    @Test
    fun `Auth service should return UserTokensDto object with jwt tokens`() {
        whenever(jwtUtil.verifyToken(any(), anyOrNull(), anyBoolean())).thenReturn(true)
        doNothing().whenever(jwtUtil).removeOldRefreshTokenByUUID(userUUID)
        whenever(jwtUtil.getUserFromToken(any())).thenReturn(dummyUser)
        whenever(jwtUtil.generateAccessToken(any())).thenReturn("accessToken")
        whenever(jwtUtil.generateRefreshToken(any())).thenReturn("refreshToken")

        val response = authService.updateTokens(UpdateUserTokensDto(
            accessToken = accessToken,
            refreshToken = refreshToken,
            uuid = userUUID
        ))

        assertThat(response).isNotNull().isEqualTo(UserTokensDto("accessToken", "refreshToken"))
        verify(jwtUtil, times(2)).verifyToken(any(), anyOrNull(), anyBoolean())
        verify(jwtUtil).removeOldRefreshTokenByUUID(userUUID)
        verify(jwtUtil).generateAccessToken(any())
        verify(jwtUtil).generateRefreshToken(any())
    }

    @Test
    fun `updateTokens should throw JwtAuthenticationException when refresh token is invalid`() {
        whenever(jwtUtil.verifyToken(any(), anyOrNull(), anyBoolean())).thenReturn(true)
        doNothing().whenever(jwtUtil).removeOldRefreshTokenByUUID(userUUID)
        whenever(jwtUtil.getUserFromToken(any())).thenReturn(null)  // Возвращаем null, чтобы вызвать исключение

        val exception = assertThrows<JwtAuthenticationException> {
            authService.updateTokens(UpdateUserTokensDto(
                accessToken = accessToken,
                refreshToken = refreshToken,
                uuid = userUUID
            ))
        }

        assertThat(exception.message).isEqualTo("Invalid refresh token")
        verify(jwtUtil, times(2)).verifyToken(any(), anyOrNull(), anyBoolean())
        verify(jwtUtil).removeOldRefreshTokenByUUID(userUUID)
        verify(jwtUtil, never()).generateAccessToken(any())
        verify(jwtUtil, never()).generateRefreshToken(any())
    }
}