package org.esc.serverportsmanager.services.interfaces

import org.esc.serverportsmanager.exceptions.NotFoundException
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager

interface CrudService<T, ID, CrDTO, UpDTO> : BasicApiService<T, ID> {
    fun getAll(): List<T> = repository.findAll()
    fun getById(id: ID, throwable: Boolean = true, message: String = "Object with id $id not found."): T? {
        val o = id!!.let { repository.findById(it) }

        if (throwable && !o.isPresent) {
            throw NotFoundException(message)
        }
        return if (!o.isPresent) null else o.get()
    }

    fun create(item: CrDTO): Any
    fun createAll(items: List<CrDTO>): Any

    fun update(item: UpDTO): Any

    @Transactional
    fun deleteById(id: ID): Any? = id?.let {
//        repository.deleteById(it)
        println(TransactionSynchronizationManager.isActualTransactionActive())
    }
    fun deleteAll(): Any? = repository.deleteAll()
}