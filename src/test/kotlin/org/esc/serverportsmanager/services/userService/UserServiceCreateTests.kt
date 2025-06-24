package org.esc.serverportsmanager.services.userService

import org.assertj.core.api.Assertions.assertThat
import org.esc.serverportsmanager.dto.users.CreateUserDto
import org.esc.serverportsmanager.entities.Users
import org.esc.serverportsmanager.entities.enums.Roles
import org.esc.serverportsmanager.exceptions.DoubleRecordException
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
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class UserServiceCreateTests {
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

    @Mock
    lateinit var repository: UsersRepository
    @Mock
    lateinit var usersMapper: UsersMapper
    @Mock
    lateinit var passwordEncoder: PasswordEncoder
    @InjectMocks
    lateinit var usersService: UsersService

    @Nested
    inner class CreateOneTests {
        @Test
        fun `should create one user`() {
            val createUserDto = CreateUserDto(
                email = TEST_EMAIL,
                username = TEST_USERNAME,
                password = TEST_PASSWORD,
            )

            whenever(repository.findByEmail(createUserDto.email)).thenReturn(null)

            val encodedPassword = "encodedPassword"
            whenever(passwordEncoder.encode(createUserDto.password)).thenReturn(encodedPassword)

            val userEntity = Users(
                id = 123L,
                email = createUserDto.email,
                username = createUserDto.username,
                password = encodedPassword,
                role = Roles.USER
            )
            whenever(usersMapper.userFromCreateDto(any())).thenReturn(
                userEntity.copy(password = encodedPassword)
            )
            whenever(repository.save(any())).thenReturn(userEntity)

            val result = usersService.create(createUserDto)

            assertThat(result).isNotNull()
            assertThat(result.email).isEqualTo(createUserDto.email)
            assertThat(result.username).isEqualTo(createUserDto.username)
            assertThat(result.password).isEqualTo(createUserDto.password)

            verify(passwordEncoder).encode(TEST_PASSWORD)
            verify(repository).findByEmail(createUserDto.email)
            verify(repository).save(any())
        }

        @Test
        fun `should throw double record exception when user with similar email exists`() {
            val createUserDto = CreateUserDto(
                email = TEST_EMAIL,
                username = TEST_USERNAME,
                password = TEST_PASSWORD,
            )
            val user = createTestUser()

            whenever(repository.findByEmail(createUserDto.email)).thenReturn(user)

            val exception = assertThrows<DoubleRecordException> {
                usersService.create(createUserDto)
            }

            assertThat(exception.message).isEqualTo("User with email $TEST_EMAIL already exists.")

        }
    }

    @Nested
    inner class CreateAllTests {
        @Test
        fun `createAll should call create for each item and return success message`() {
            usersService = spy(UsersService(repository, usersMapper, passwordEncoder))

            val dtoList = listOf(
                CreateUserDto(email = "user1@example.com", username = "user1", password = "pass1"),
                CreateUserDto(email = "user2@example.com", username = "user2", password = "pass2")
            )
            val dummyUser = createTestUser()
            doReturn(dummyUser).`when`(usersService).create(any())

            val result = usersService.createAll(dtoList)

            verify(usersService, times(dtoList.size)).create(any())
            assertThat(result).isEqualTo("Users created")
        }

        @Test
        fun `createAll should throw exception when user with similar email exists`() {
            usersService = spy(UsersService(repository, usersMapper, passwordEncoder))

            val dtoList = listOf(
                CreateUserDto(email = "user1@example.com", username = "user1", password = "pass1"),
                CreateUserDto(email = "user2@example.com", username = "user2", password = "pass2")
            )

            doReturn(createTestUser(email = "user1@example.com"))
                .doThrow(DoubleRecordException("User with email user2@example.com already exists."))
                .`when`(usersService).create(any())

            val exception = assertThrows<DoubleRecordException> {
                usersService.createAll(dtoList)
            }

            assertThat(exception.message).isEqualTo("User with email user2@example.com already exists.")
            verify(usersService, times(2)).create(any())
        }

    }
}