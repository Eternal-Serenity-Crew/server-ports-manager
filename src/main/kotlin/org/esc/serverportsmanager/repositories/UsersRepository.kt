package org.esc.serverportsmanager.repositories

import org.esc.serverportsmanager.entities.Users
import org.springframework.data.jpa.repository.JpaRepository

interface UsersRepository: JpaRepository<Users, Long> {
    fun findByEmail(email: String): Users?
}