package org.esc.serverportsmanager.controllers

import org.esc.serverportsmanager.controllers.interfaces.CrudController
import org.esc.serverportsmanager.dto.ports.CreatePortDto
import org.esc.serverportsmanager.dto.ports.UpdatePortDto
import org.esc.serverportsmanager.entities.PortsStorage
import org.esc.serverportsmanager.io.BasicSuccessfulResponse
import org.esc.serverportsmanager.io.converters.toHttpResponse
import org.esc.serverportsmanager.repositories.PortsStorageRepository
import org.esc.serverportsmanager.services.PortsService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController("/api/v1/ports")
class PortsController(
    override val service: PortsService,
    override val repository: PortsStorageRepository,
) : CrudController<PortsStorage, Long, CreatePortDto, UpdatePortDto> {

    @GetMapping("/")
    override fun getAll() = service.getAll().toHttpResponse()

    @GetMapping("/{id}")
    override fun getById(@PathVariable id: Long) =
        service.getById(id)!!.toHttpResponse()

    @PostMapping("/")
    override fun create(@RequestBody item: CreatePortDto) =
        service.create(item).toHttpResponse()

    @PostMapping("/createAll")
    override fun createAll(@RequestBody items: List<CreatePortDto>) =
        service.createAll(items).toHttpResponse()

    @PatchMapping("/")
    override fun update(@RequestBody item: UpdatePortDto) =
        service.update(item).toHttpResponse()

    @DeleteMapping("/{id}")
    override fun deleteById(@PathVariable id: Long) =
        service.deleteById(id).toHttpResponse()

    @DeleteMapping("/deleteAll")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    override fun deleteAll(): BasicSuccessfulResponse<String> {
        service.deleteAll()
        return BasicSuccessfulResponse("Users deleted")
    }
}