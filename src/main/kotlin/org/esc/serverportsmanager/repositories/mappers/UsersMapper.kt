package org.esc.serverportsmanager.repositories.mappers

import org.esc.serverportsmanager.dto.users.CreateUserDto
import org.esc.serverportsmanager.entities.Users
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface UsersMapper {
    fun userFromCreateDto(o: CreateUserDto): Users
}