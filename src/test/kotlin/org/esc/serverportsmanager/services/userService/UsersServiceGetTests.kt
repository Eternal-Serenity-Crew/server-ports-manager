package org.esc.serverportsmanager.services.userService

import org.assertj.core.api.Assertions.assertThat
import org.esc.serverportsmanager.entities.Users
import org.esc.serverportsmanager.entities.enums.Roles
import org.esc.serverportsmanager.exceptions.NotFoundException
import org.esc.serverportsmanager.repositories.UsersRepository
import org.esc.serverportsmanager.repositories.mappers.UsersMapper
import org.esc.serverportsmanager.services.UsersService
import org.esc.serverportsmanager.services.UserData.TEST_EMAIL
import org.esc.serverportsmanager.services.UserData.TEST_USERNAME
import org.esc.serverportsmanager.services.UserData.USER_ID1
import org.esc.serverportsmanager.services.UserData.USER_ID2
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class UsersServiceGetTests {
    private fun createTestUser(
        id: Long = 1L,
        email: String = TEST_EMAIL,
        username: String = TEST_USERNAME
    ): Users {
        val TEST_PASSWORD = ""
        return Users(
            id = id,
            email = email,
            username = username,
            password = TEST_PASSWORD,
            role = Roles.USER
        )
    }

    @Mock lateinit var repository: UsersRepository
    @Mock lateinit var usersMapper: UsersMapper
    @Mock lateinit var passwordEncoder: PasswordEncoder
    @InjectMocks lateinit var usersService: UsersService


    @Nested
    inner class GetByIdTests {
        @Test
        fun `getById should return user when found`() {
            val user = createTestUser(1L)

            whenever(repository.findById(USER_ID1)).thenReturn(Optional.of(user))

            val result = usersService.getById(USER_ID1)

            assertThat(result).isNotNull().isEqualTo(user)
        }

        @Test
        fun `getById should throw NotFoundException when user not found and throwable true`() {
            whenever(repository.findById(USER_ID2)).thenReturn(Optional.empty())

            val exception = assertThrows<NotFoundException> {
                usersService.getById(USER_ID2, throwable = true)
            }

            assertEquals("Object with id $USER_ID2 not found.", exception.message)
        }

        @Test
        fun `getById should return null when user not found and throwable false`() {
            whenever(repository.findById(USER_ID2)).thenReturn(Optional.empty())

            val result = usersService.getById(USER_ID2, throwable = false)

            assertThat(result).isNull()
        }
    }

    @Nested
    inner class GetByEmailTests {
        @Test
        fun `getByEmail should return user when found`() {
            val user = createTestUser(1L)

            whenever(repository.findByEmail(TEST_EMAIL)).thenReturn(user)

            val result = usersService.getByEmail(TEST_EMAIL)

            assertThat(result).isNotNull().isEqualTo(user)
        }

        @Test
        fun `getByEmail should throw NotFoundException when user not found and throwable true`() {
            whenever(repository.findByEmail(TEST_EMAIL)).thenReturn(null)

            val exception = assertThrows<NotFoundException> {
                usersService.getByEmail(TEST_EMAIL, throwable = true)
            }

            assertEquals("User with email $TEST_EMAIL not found.", exception.message)
        }

        @Test
        fun `getByEmail should return null when user not found and throwable false`() {
            whenever(repository.findByEmail(TEST_EMAIL)).thenReturn(null)

            val result = usersService.getByEmail(TEST_EMAIL, throwable = false)

            assertThat(result).isNull()
        }
    }

    @Nested
    inner class GetAllTests {
        @Test
        fun `getAll should return all users when found`() {
            val user1 = createTestUser(1L)
            val user2 = createTestUser(2L)

            whenever(repository.findAll()).thenReturn(listOf(user1, user2))

            val result = usersService.getAll()

            assertThat(result).isNotNull().isEqualTo(listOf(user1, user2))
        }

        @Test
        fun `getAll should return empty list when users not defined`() {
            whenever(repository.findAll()).thenReturn(emptyList())

            val result = usersService.getAll()

            assertThat(result).isNotNull().isEqualTo(emptyList<Users>())
        }
    }
}