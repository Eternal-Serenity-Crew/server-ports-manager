package org.esc.serverportsmanager.io.converters

import org.esc.serverportsmanager.io.BasicSuccessfulResponse

fun <T> List<T>.toHttpResponse(): BasicSuccessfulResponse<List<T>> {
    return BasicSuccessfulResponse(this)
}