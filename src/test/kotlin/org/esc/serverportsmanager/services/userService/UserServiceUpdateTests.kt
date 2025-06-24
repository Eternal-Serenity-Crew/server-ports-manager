package org.esc.serverportsmanager.services.userService

import org.assertj.core.api.Assertions.assertThat
import org.esc.serverportsmanager.dto.users.UpdateUserDto
import org.esc.serverportsmanager.entities.Users
import org.esc.serverportsmanager.entities.enums.Roles
import org.esc.serverportsmanager.exceptions.DoubleRecordException
import org.esc.serverportsmanager.exceptions.NotFoundException
import org.esc.serverportsmanager.repositories.UsersRepository
import org.esc.serverportsmanager.repositories.mappers.UsersMapper
import org.esc.serverportsmanager.services.UsersService
import org.esc.serverportsmanager.services.userService.UserData.TEST_EMAIL
import org.esc.serverportsmanager.services.userService.UserData.TEST_PASSWORD
import org.esc.serverportsmanager.services.userService.UserData.TEST_USERNAME
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class UserServiceUpdateTests {
    @Mock lateinit var repository: UsersRepository
    @Mock lateinit var userMapper: UsersMapper
    @Mock lateinit var passwordEncoder: PasswordEncoder
    @InjectMocks lateinit var usersService: UsersService

    private fun createTestUser(
        id: Long = 1L,
        email: String = "old@example.com",
        username: String = "oldUser",
        password: String = "oldPassword"
    ) = Users(
        id = id,
        email = email,
        username = username,
        password = password,
        role = Roles.USER
    )

    @Nested
    inner class UpdateTests {
        @Test
        fun `update should successfully update user data`() {
            val userId = 1L
            val existingUser = createTestUser(id = userId)

            val updateDto = UpdateUserDto(
                id = userId,
                username = TEST_USERNAME,
                email = TEST_EMAIL,
                password = TEST_PASSWORD
            )

            whenever(repository.findById(userId)).thenReturn(Optional.of(existingUser))
            whenever(repository.findByEmail(updateDto.email!!)).thenReturn(null)
            whenever(passwordEncoder.encode(updateDto.password!!)).thenReturn("encodedNewPassword")

            val result = usersService.update(updateDto)

            assertThat(existingUser.username).isEqualTo(updateDto.username)
            assertThat(existingUser.email).isEqualTo(updateDto.email)
            assertThat(existingUser.password).isEqualTo("encodedNewPassword")

            // Проверяем возвращаемое сообщение
            assertThat(result).isEqualTo("User data updated successfully")

            // Проверяем вызовы
            verify(repository).findById(userId)
            verify(repository).findByEmail(updateDto.email)
            verify(passwordEncoder).encode(updateDto.password)
        }

        @Test
        fun `update should throw DoubleRecordException if email already exists for another user`() {
            val userId = 1L
            val existingUser = createTestUser(id = userId)
            val otherUser = createTestUser(id = 2L, email = "new@example.com")

            val updateDto = UpdateUserDto(
                id = userId,
                email = TEST_EMAIL,
            )

            whenever(repository.findById(userId)).thenReturn(Optional.of(existingUser))
            whenever(repository.findByEmail(updateDto.email!!)).thenReturn(otherUser)

            val exception = assertThrows<DoubleRecordException> {
                usersService.update(updateDto)
            }

            assertThat(exception.message).isEqualTo("User with email ${updateDto.email} already exists.")

            verify(repository).findById(userId)
            verify(repository).findByEmail(updateDto.email)
            verify(passwordEncoder, never()).encode(any())
        }

        @Test
        fun `update should throw NotFoundException if user does not exist`() {
            val userId = 1L
            val updateDto = UpdateUserDto(
                id = userId
            )

            whenever(repository.findById(userId)).thenReturn(Optional.empty())

            val exception = assertThrows<NotFoundException> {
                usersService.update(updateDto)
            }

            assertThat(exception.message).isEqualTo("User with id $userId does not exist.")

            verify(repository).findById(userId)
            verify(repository, never()).findByEmail(any())
            verify(passwordEncoder, never()).encode(any())
        }
    }
}