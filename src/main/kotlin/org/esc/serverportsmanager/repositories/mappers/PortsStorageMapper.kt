package org.esc.serverportsmanager.repositories.mappers

import org.esc.serverportsmanager.dto.ports.CreatePortDto
import org.esc.serverportsmanager.entities.PortsStorage
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface PortsStorageMapper {
    fun portFromCreateDto(o: CreatePortDto): PortsStorage
}