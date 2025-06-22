package org.esc.serverportsmanager.repositories.mappers

import org.esc.serverportsmanager.dto.jwt.SaveRefreshTokenDto
import org.esc.serverportsmanager.entities.JwtTokensStorage
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface JwtTokensStorageMapper {
    fun tokenFromSaveRefreshDto(o: SaveRefreshTokenDto): JwtTokensStorage
}