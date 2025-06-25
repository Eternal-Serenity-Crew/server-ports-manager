package org.esc.serverportsmanager.services.userService

import org.assertj.core.api.Assertions.assertThat
import org.esc.serverportsmanager.entities.Users
import org.esc.serverportsmanager.entities.enums.Roles
import org.esc.serverportsmanager.exceptions.NotFoundException
import org.esc.serverportsmanager.repositories.UsersRepository
import org.esc.serverportsmanager.repositories.mappers.UsersMapper
import org.esc.serverportsmanager.services.UsersService
import org.esc.serverportsmanager.services.UserData.TEST_EMAIL
import org.esc.serverportsmanager.services.UserData.TEST_PASSWORD
import org.esc.serverportsmanager.services.UserData.TEST_USERNAME
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
class UserServiceDeleteTests {
    @Mock lateinit var repository: UsersRepository
    @Mock lateinit var usersMapper: UsersMapper
    @Mock lateinit var passwordEncoder: PasswordEncoder
    @InjectMocks lateinit var usersService: UsersService

    private fun createTestUser(id: Long) = Users(
        id = id,
        email = TEST_EMAIL,
        username = TEST_USERNAME,
        password = TEST_PASSWORD,
        role = Roles.USER
    )

    @Nested
    inner class DeleteOneTests {
        @Test
        fun `deleteById should delete user when user exists`() {
            val userId = 1L
            val existingUser = createTestUser(userId)

            whenever(repository.findById(userId)).thenReturn(Optional.of(existingUser))

            val result = usersService.deleteById(userId)

            verify(repository).deleteById(userId)
            assertThat(result).isEqualTo("User with id $userId deleted successfully")
        }

        @Test
        fun `deleteById should throw NotFoundException when user does not exist`() {
            val userId = 2L

            whenever(repository.findById(userId)).thenReturn(Optional.empty())

            val exception = assertThrows<NotFoundException> {
                usersService.deleteById(userId)
            }

            assertThat(exception.message).isEqualTo("User with id $userId not found.")
            verify(repository, never()).deleteById(any())
        }
    }
}