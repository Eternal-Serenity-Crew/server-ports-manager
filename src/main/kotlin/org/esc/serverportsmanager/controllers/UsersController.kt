package org.esc.serverportsmanager.controllers

import org.esc.serverportsmanager.controllers.interfaces.CrudController
import org.esc.serverportsmanager.dto.users.CreateUserDto
import org.esc.serverportsmanager.dto.users.UpdateUserDto
import org.esc.serverportsmanager.entities.Users
import org.esc.serverportsmanager.io.BasicSuccessfulResponse
import org.esc.serverportsmanager.io.converters.toHttpResponse
import org.esc.serverportsmanager.repositories.UsersRepository
import org.esc.serverportsmanager.services.UsersService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UsersController(
    override val service: UsersService,
    override val repository: UsersRepository,
) : CrudController<Users, Long, CreateUserDto, UpdateUserDto> {

    @GetMapping("/")
    override fun getAll(): BasicSuccessfulResponse<List<Users>> = service.getAll().toHttpResponse()

    @GetMapping("/{id}")
    override fun getById(@PathVariable id: Long): BasicSuccessfulResponse<Users> {
        return service.getById(id, message = "User with id $id not found.")!!.toHttpResponse()
    }

    @GetMapping("/getByEmail/{email}")
    fun getByEmail(@PathVariable email: String): BasicSuccessfulResponse<Users?> {
        return BasicSuccessfulResponse(service.getByEmail(email))
    }

    @PostMapping("/")
    override fun create(@RequestBody item: CreateUserDto): BasicSuccessfulResponse<Users> {
        return BasicSuccessfulResponse(service.create(item))
    }

    @PostMapping("/createAll")
    override fun createAll(@RequestBody items: List<CreateUserDto>): BasicSuccessfulResponse<String> {
        return BasicSuccessfulResponse(service.createAll(items))
    }

    @PatchMapping("/")
    override fun update(@RequestBody item: UpdateUserDto): BasicSuccessfulResponse<*> {
        return BasicSuccessfulResponse(service.update(item))
    }

    @DeleteMapping("/{id}")
    override fun deleteById(@PathVariable id: Long): BasicSuccessfulResponse<*> {
        return BasicSuccessfulResponse(service.deleteById(id))
    }

    @DeleteMapping("/")
    override fun deleteAll(): BasicSuccessfulResponse<*> {
        service.deleteAll()
        return BasicSuccessfulResponse("Users deleted")
    }
}