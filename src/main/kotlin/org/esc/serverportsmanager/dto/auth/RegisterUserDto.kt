package org.esc.serverportsmanager.dto.auth

import org.esc.serverportsmanager.dto.DtoClass
import org.esc.serverportsmanager.entities.enums.Roles
import java.util.UUID

data class RegisterUserDto(
    val username: String,
    val email: String,
    var password: String,
    val role: Roles = Roles.USER,
    val uuid: UUID
) : DtoClass
