package org.esc.serverportsmanager.services

import org.esc.serverportsmanager.dto.ports.CreatePortDto
import org.esc.serverportsmanager.dto.ports.CreatePortDtoForMapper
import org.esc.serverportsmanager.dto.ports.UpdatePortDto
import org.esc.serverportsmanager.entities.PortsStorage
import org.esc.serverportsmanager.exceptions.DoubleRecordException
import org.esc.serverportsmanager.repositories.PortsStorageRepository
import org.esc.serverportsmanager.repositories.mappers.PortsStorageMapper
import org.esc.serverportsmanager.services.interfaces.CrudService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PortsService(
    override val repository: PortsStorageRepository,
    private val portsStorageMapper: PortsStorageMapper,
    private val usersService: UsersService,
) : CrudService<PortsStorage, Long, CreatePortDto, UpdatePortDto> {

    fun getByPortNumber(portNumber: Long, throwable: Boolean = true): PortsStorage? {
        val port = repository.findByPortNumber(portNumber)

        if (port != null && throwable) {
            throw DoubleRecordException("Port $portNumber already defined")
        }

        return port
    }

    @Transactional
    override fun create(item: CreatePortDto): PortsStorage {
        getByPortNumber(item.portNumber)
        val user = usersService.getById(item.userId, message = "User with id '${item.userId}' not found")!!

        val o = CreatePortDtoForMapper(
            portNumber = item.portNumber,
            name = item.name,
            description = item.description,
            user = user,
        )

        return portsStorageMapper.portFromCreateDto(o).run { repository.save(this) }
    }

    @Transactional
    override fun createAll(items: List<CreatePortDto>): String {
        for (item in items) {
            create(item)
        }

        return "All ports have been created."
    }

    @Transactional
    override fun update(item: UpdatePortDto): String {
        val port = getById(item.id, message = "Port with id '${item.id}' wasn't found")!!

        item.portNumber?.let { port.portNumber = it }
        item.name?.let { port.name = it }
        item.description?.let { port.description = it }

        return "Port data updated successfully."
    }

    @Transactional
    override fun deleteById(id: Long): Any? {
        getById(id, message = "Port with id '$id' wasn't found")!!.let { repository.deleteById(it.id) }

        return "Port with id '${id}' was deleted successfully."
    }
}