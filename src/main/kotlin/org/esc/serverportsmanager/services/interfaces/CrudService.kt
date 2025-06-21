package org.esc.serverportsmanager.services.interfaces

import org.esc.serverportsmanager.dto.DtoClass
import org.springframework.data.jpa.repository.JpaRepository

interface CrudService<T, ID> {
    val repository: JpaRepository<T, ID>

    fun getAll(): List<T> = repository.findAll()
    fun getAllByIds(ids: List<ID>): List<T> = repository.findAllById(ids)
    fun getById(id: ID): T? = id?.let { repository.findById(it) }?.get()

    fun <D: DtoClass> create(item: D)
    fun <D: DtoClass> createAll(items: List<D>)

    fun <D: DtoClass> update(item: D)

    fun deleteById(id: ID) = id?.let { repository.deleteById(it) }
    fun deleteAll() = repository.deleteAll()
}