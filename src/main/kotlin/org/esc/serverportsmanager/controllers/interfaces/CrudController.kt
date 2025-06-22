package org.esc.serverportsmanager.controllers.interfaces

import org.esc.serverportsmanager.io.BasicSuccessfulResponse
import org.esc.serverportsmanager.services.interfaces.CrudService
import org.springframework.data.jpa.repository.JpaRepository

interface CrudController<T, ID, CrDTO, UpDTO> : BasicRestController {
    override val service: CrudService<T, ID, CrDTO, UpDTO>
    override val repository: JpaRepository<T, ID>
    fun getAll(): List<T>
    fun getById(id: ID): T?

    fun create(item: CrDTO) : BasicSuccessfulResponse<*>
    fun createAll(items: List<CrDTO>) : BasicSuccessfulResponse<*>

    fun update(item: UpDTO) : BasicSuccessfulResponse<*>

    fun deleteById(id: ID) : BasicSuccessfulResponse<*>
    fun deleteAll() : BasicSuccessfulResponse<*>
}