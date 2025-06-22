package org.esc.serverportsmanager.controllers.interfaces

import org.esc.serverportsmanager.repositories.mappers.AbstractMapper
import org.esc.serverportsmanager.services.interfaces.BasicApiService
import org.springframework.data.jpa.repository.JpaRepository

interface BasicRestController {
    val service: BasicApiService<*, *>
    val repository: JpaRepository<*, *>
}