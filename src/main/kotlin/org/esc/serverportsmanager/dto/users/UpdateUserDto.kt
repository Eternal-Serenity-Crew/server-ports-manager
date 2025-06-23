package org.esc.serverportsmanager.dto.users

import org.esc.serverportsmanager.dto.DtoClass

data class UpdateUserDto(
    val id: Long,
    val username: String?,
    val email: String?,
    val password: String?
) : DtoClass
