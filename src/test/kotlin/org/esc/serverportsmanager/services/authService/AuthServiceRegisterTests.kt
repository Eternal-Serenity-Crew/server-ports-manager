package org.esc.serverportsmanager.services.authService

import org.assertj.core.api.Assertions.assertThat
import org.esc.serverportsmanager.dto.auth.RegisterUserDto
import org.esc.serverportsmanager.dto.jwt.CreateJwtToken
import org.esc.serverportsmanager.dto.jwt.UserTokensDto
import org.esc.serverportsmanager.entities.Users
import org.esc.serverportsmanager.entities.enums.Roles
import org.esc.serverportsmanager.exceptions.DoubleRecordException
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class AuthServiceRegisterTests {
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
        whenever(userService.create(any())).thenReturn(dummyUser)
        doNothing().whenever(jwtUtil).removeOldRefreshTokenByUUID(userUUID)
        whenever(jwtUtil.generateAccessToken(any())).thenReturn("accessToken")
        whenever(jwtUtil.generateRefreshToken(any())).thenReturn("refreshToken")

        val response = authService.register(
            RegisterUserDto(
                username = TEST_USERNAME,
                email = TEST_EMAIL,
                password = TEST_PASSWORD,
                role = Roles.USER,
                uuid = userUUID
            )
        )

        assertThat(response).isNotNull().isEqualTo(UserTokensDto("accessToken", "refreshToken"))
        verify(userService).create(any())
        verify(jwtUtil).removeOldRefreshTokenByUUID(userUUID)
        verify(jwtUtil).generateAccessToken(CreateJwtToken(dummyUser, userUUID))
        verify(jwtUtil).generateRefreshToken(CreateJwtToken(dummyUser, userUUID))
    }

    @Test
    fun `Auth service should throw DoubleRecordException when try to register new user`() {
        whenever(userService.create(any())).thenThrow(DoubleRecordException("User with email $TEST_EMAIL already exists."))

        val exception = assertThrows<DoubleRecordException> {
            authService.register(RegisterUserDto(
                username = TEST_USERNAME,
                email = TEST_EMAIL,
                password = TEST_PASSWORD,
                role = Roles.USER,
                uuid = userUUID
            ))
        }

        assertThat(exception).isInstanceOf(DoubleRecordException::class.java)
        assertThat(exception.message).isEqualTo("User with email $TEST_EMAIL already exists.")
    }
}