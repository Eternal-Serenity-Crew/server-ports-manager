package org.esc.serverportsmanager.dto.jwt

import org.esc.serverportsmanager.dto.DtoClass

data class UserTokensDto(
    val accessToken: String,
    val refreshToken: String,
) : DtoClass
