package org.esc.serverportsmanager.repositories

import org.esc.serverportsmanager.entities.JwtTokensStorage
import org.springframework.data.jpa.repository.JpaRepository

interface JwtTokensRepository : JpaRepository<JwtTokensStorage, Long> {
    fun deleteByToken(token: String)
}