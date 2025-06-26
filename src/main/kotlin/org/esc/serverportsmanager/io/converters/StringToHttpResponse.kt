package org.esc.serverportsmanager.io.converters

import org.esc.serverportsmanager.io.BasicSuccessfulResponse

fun String.toHttpResponse(): BasicSuccessfulResponse<String> {
    return BasicSuccessfulResponse(this)
}