package org.esc.serverportsmanager.validators

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class CompareDtoClassesAspect {

    @Before("@annotation(CompareDtoClasses)")
    fun beforeClass(joinPoint: JoinPoint) {
        val method = (joinPoint.signature as MethodSignature).method
        val annotation = method.getAnnotation(CompareDtoClasses::class.java)
        val expectedClass = annotation.value.java

        val args = joinPoint.args
        if (args.isEmpty()) {
            throw IllegalArgumentException("The method must accept at least one parameter to check the DTO.")
        }

        val dto = args[0]
        if (!expectedClass.isInstance(dto)) {
            throw IllegalArgumentException(
                "Invalid DTO type. Expected: ${expectedClass.simpleName}, received: ${dto::class.java.simpleName}"
            )
        }
    }
}