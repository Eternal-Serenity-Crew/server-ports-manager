package org.esc.serverportsmanager.services.authService

import org.assertj.core.api.Assertions.assertThat
import org.esc.serverportsmanager.dto.auth.LoginUserDto
import org.esc.serverportsmanager.dto.jwt.UserTokensDto
import org.esc.serverportsmanager.entities.Users
import org.esc.serverportsmanager.entities.enums.Roles
import org.esc.serverportsmanager.exceptions.JwtAuthenticationException
import org.esc.serverportsmanager.exceptions.NotFoundException
import org.esc.serverportsmanager.services.AuthService
import org.esc.serverportsmanager.services.UserData.TEST_EMAIL
import org.esc.serverportsmanager.services.UserData.TEST_PASSWORD
import org.esc.serverportsmanager.services.UserData.TEST_USERNAME
import org.esc.serverportsmanager.services.UsersService
import org.esc.serverportsmanager.utils.JwtUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class AuthServiceLoginTests {
    @Mock
    private lateinit var userService: UsersService
    @Mock
    private lateinit var jwtUtil: JwtUtil
    @Mock
    private lateinit var passwordEncoder: PasswordEncoder
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

    @Test
    fun `Auth service should return UserTokensDto object with jwt tokens`() {
        whenever(userService.getByEmail(any(), eq(true))).thenReturn(dummyUser)
        whenever(passwordEncoder.matches(TEST_PASSWORD, dummyUser.password)).thenReturn(true)
        doNothing().whenever(jwtUtil).removeOldRefreshTokenByUUID(userUUID)
        whenever(jwtUtil.generateAccessToken(any())).thenReturn("accessToken")
        whenever(jwtUtil.generateRefreshToken(any())).thenReturn("refreshToken")

        val response = authService.login(
            LoginUserDto(
                email = TEST_EMAIL,
                password = TEST_PASSWORD,
                uuid = userUUID
            )
        )

        assertThat(response).isNotNull().isEqualTo(UserTokensDto("accessToken", "refreshToken"))
        verify(userService).getByEmail(any(), eq(true))
        verify(jwtUtil).removeOldRefreshTokenByUUID(userUUID)
        verify(jwtUtil).generateAccessToken(any())
        verify(jwtUtil).generateRefreshToken(any())
    }

    @Test
    fun `Auth service should throw JwtAuthenticationException when credits are incorrect`() {
        whenever(userService.getByEmail(any(), eq(true))).thenReturn(dummyUser)
        whenever(passwordEncoder.matches(TEST_PASSWORD, dummyUser.password)).thenReturn(false)

        val exception = assertThrows<JwtAuthenticationException> {
            authService.login(
                LoginUserDto(
                    email = TEST_EMAIL,
                    password = TEST_PASSWORD,
                    uuid = userUUID
                )
            )
        }

        assertThat(exception).isInstanceOf(JwtAuthenticationException::class.java)
        assertThat(exception.message).isEqualTo("Wrong email or password")
        verify(userService).getByEmail(any(), eq(true))
    }

    @Test
    fun `Auth service should throw NotFoundException when no user exists`() {
        whenever(userService.getByEmail(any(), eq(true))).thenThrow(NotFoundException("User with email $TEST_EMAIL not found."))

        val exception = assertThrows<NotFoundException> {
            authService.login(
                LoginUserDto(
                    email = TEST_EMAIL,
                    password = TEST_PASSWORD,
                    uuid = userUUID
                )
            )
        }

        assertThat(exception).isInstanceOf(NotFoundException::class.java)
        assertThat(exception.message).isEqualTo("User with email $TEST_EMAIL not found.")
        verify(userService).getByEmail(any(), eq(true))
    }
}