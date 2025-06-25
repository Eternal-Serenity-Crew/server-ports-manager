package org.esc.serverportsmanager.repositories

import org.esc.serverportsmanager.entities.JwtTokensStorage
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JwtTokensRepository : JpaRepository<JwtTokensStorage, Long> {
    fun findByUuid(uuid: UUID): JwtTokensStorage?
    fun deleteByToken(token: String)
    fun deleteByUuid(uuid: UUID)
}