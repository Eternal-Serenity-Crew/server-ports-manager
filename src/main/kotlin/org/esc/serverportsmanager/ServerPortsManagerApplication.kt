package org.esc.serverportsmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ServerPortsManagerApplication

fun main(args: Array<String>) {
    runApplication<ServerPortsManagerApplication>(*args)
}
