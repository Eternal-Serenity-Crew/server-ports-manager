package org.esc.serverportsmanager.dto.jwt

import org.esc.serverportsmanager.entities.Users
import java.util.UUID

data class SaveRefreshTokenDto(val user: Users, val uuid: UUID, val token: String)
