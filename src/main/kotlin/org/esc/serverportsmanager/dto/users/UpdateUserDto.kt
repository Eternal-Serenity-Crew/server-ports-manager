package org.esc.serverportsmanager.dto.users

import org.esc.serverportsmanager.dto.DtoClass

data class UpdateUserDto(
    val id: Long,
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
) : DtoClass
