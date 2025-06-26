package org.esc.serverportsmanager.repositories

import org.esc.serverportsmanager.entities.PortsStorage
import org.springframework.data.jpa.repository.JpaRepository

interface PortsStorageRepository : JpaRepository<PortsStorage, Long> {
    fun findByPortNumber(portNumber: Long): PortsStorage?
}