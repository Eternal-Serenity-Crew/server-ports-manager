package org.esc.serverportsmanager.services

import org.esc.serverportsmanager.entities.Users
import org.esc.serverportsmanager.repositories.UsersRepository
import org.esc.serverportsmanager.dto.users.CreateUserDto
import org.esc.serverportsmanager.dto.users.UpdateUserDto
import org.esc.serverportsmanager.exceptions.DoubleRecordException
import org.esc.serverportsmanager.exceptions.NotFoundException
import org.esc.serverportsmanager.repositories.mappers.UsersMapper
import org.esc.serverportsmanager.services.interfaces.CrudService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UsersService(
    override val repository: UsersRepository,
    private val usersMapper: UsersMapper,
    private val passwordEncoder: PasswordEncoder,
) : CrudService<Users, Long, CreateUserDto, UpdateUserDto> {

    fun getByEmail(email: String, throwable: Boolean = true): Users? {
        val o = repository.findByEmail(email)

        if (o == null && throwable) throw NotFoundException("User with email $email not found.")
        return o
    }

    @Transactional
    override fun create(item: CreateUserDto): Users {
        getByEmail(item.email, throwable = false)?.run {
            throw DoubleRecordException("User with email ${item.email} already exists.")
        } ?: usersMapper.userFromCreateDto(
            item.apply { password = passwordEncoder.encode(item.password) }
        ).let { return repository.save(it) }
    }

    @Transactional
    override fun createAll(items: List<CreateUserDto>): String {
        items.forEach { create(it) }
        return "Users created"
    }

    @Transactional
    override fun update(item: UpdateUserDto): String {
        val user = getById(item.id, message = "User with id ${item.id} does not exist.")!!

        item.username?.let { user.username = it }

        item.email?.let { newEmail ->
            repository.findByEmail(newEmail)?.let {
                if (it.id != user.id) {
                    throw DoubleRecordException("User with email $newEmail already exists.")
                }
            }
            user.email = newEmail
        }

        item.password?.let { newPassword ->
            user.password = passwordEncoder.encode(newPassword)
        }

        return "User data updated successfully"
    }

    @Transactional
    override fun deleteById(id: Long): String {
        getById(id, throwable = false)?.let {
            repository.deleteById(id)
            return "User with id $id deleted successfully"
        } ?: throw NotFoundException("User with id $id not found.")
    }
}