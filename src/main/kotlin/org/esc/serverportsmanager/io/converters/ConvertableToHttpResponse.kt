@file:Suppress("UNCHECKED_CAST")

package org.esc.serverportsmanager.io.converters

import org.esc.serverportsmanager.io.BasicSuccessfulResponse

interface ConvertableToHttpResponse<T : ConvertableToHttpResponse<T>> {
    fun toHttpResponse(): BasicSuccessfulResponse<T> = BasicSuccessfulResponse(this as T)
}