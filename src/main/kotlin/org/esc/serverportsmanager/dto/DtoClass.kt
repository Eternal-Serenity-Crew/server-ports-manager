package org.esc.serverportsmanager.dto

import org.esc.serverportsmanager.io.BasicSuccessfulResponse

interface DtoClass

fun <T : DtoClass> T.toHttpResponse(): BasicSuccessfulResponse<T> {
    return BasicSuccessfulResponse(this)
}