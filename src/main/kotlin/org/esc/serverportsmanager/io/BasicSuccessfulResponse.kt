package org.esc.serverportsmanager.io

import org.springframework.http.HttpStatus

data class BasicSuccessfulResponse<T>(
    override val message: T
) : AbstractResponse<T>(HttpStatus.OK.value(), message)