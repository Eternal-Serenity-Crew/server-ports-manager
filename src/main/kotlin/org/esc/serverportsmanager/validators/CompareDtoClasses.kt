package org.esc.serverportsmanager.validators

import org.esc.serverportsmanager.dto.DtoClass
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CompareDtoClasses(val value: KClass<out DtoClass>)
