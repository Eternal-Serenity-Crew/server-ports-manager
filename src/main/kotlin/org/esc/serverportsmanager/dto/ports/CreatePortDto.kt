package org.esc.serverportsmanager.dto.ports

import org.esc.serverportsmanager.dto.DtoClass

data class CreatePortDto(
    val portNumber: Long,
    val name: String,
    val description: String,
    val userId: Long
) : DtoClass
