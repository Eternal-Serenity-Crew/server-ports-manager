package org.esc.serverportsmanager.dto.ports

import org.esc.serverportsmanager.dto.DtoClass
import org.esc.serverportsmanager.entities.Users

data class CreatePortDtoForMapper(
    val portNumber: Long,
    val name: String,
    val description: String,
    val user: Users
) : DtoClass
