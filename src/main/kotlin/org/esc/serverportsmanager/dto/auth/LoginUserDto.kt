package org.esc.serverportsmanager.dto.auth

import org.esc.serverportsmanager.dto.DtoClass
import java.util.UUID

data class LoginUserDto(
    val email: String,
    val password: String,
    val uuid: UUID,
) : DtoClass