package org.esc.serverportsmanager.dto.ports

import org.esc.serverportsmanager.dto.DtoClass

data class UpdatePortDto(
    val id: Long,
    val portNumber: Long?,
    val name: String?,
    val description: String?,
) : DtoClass
