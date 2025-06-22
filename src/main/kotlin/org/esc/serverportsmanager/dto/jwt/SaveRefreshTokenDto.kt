package org.esc.serverportsmanager.dto.jwt

import java.util.UUID

data class SaveRefreshTokenDto(val userId: Long, val uuid: UUID, val token: String)
