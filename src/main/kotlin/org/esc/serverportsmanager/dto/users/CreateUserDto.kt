package org.esc.serverportsmanager.dto.users

import org.esc.serverportsmanager.entities.enums.Roles
import org.esc.serverportsmanager.dto.DtoClass

data class CreateUserDto(
    val username: String,
    val email: String,
    var password: String,
    val role: Roles = Roles.USER
) : DtoClass
