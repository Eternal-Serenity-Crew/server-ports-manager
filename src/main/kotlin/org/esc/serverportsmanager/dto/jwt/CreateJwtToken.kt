package org.esc.serverportsmanager.dto.jwt

import org.esc.serverportsmanager.entities.Users
import java.util.UUID

data class CreateJwtToken(
    val user: Users,
    val uuid: UUID,
)