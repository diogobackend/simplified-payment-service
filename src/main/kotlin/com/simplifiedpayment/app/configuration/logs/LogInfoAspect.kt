package com.simplifiedpayment.app.configuration.logs

import mu.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Aspect
@Component
class LogInfoAspect {
    private val log = KotlinLogging.logger {}

    @Around("@annotation(logInfo)")
    fun logMethod(
        joinPoint: ProceedingJoinPoint,
        logInfo: LogInfo,
    ): Any? {
        val signature = joinPoint.signature as MethodSignature
        val method =
            joinPoint.target.javaClass
                .methods
                .first { it.name == signature.method.name }

        val result = joinPoint.proceed()

        val className =
            joinPoint.target.javaClass.simpleName
                .substringBefore("$")

        log.info {
            buildLogMessage(
                className = className,
                method = method,
                args = joinPoint.args,
                result = result,
                logInfo = logInfo,
            )
        }

        return result
    }

    private fun buildLogMessage(
        className: String,
        method: Method,
        args: Array<Any?>,
        result: Any?,
        logInfo: LogInfo,
    ): String {
        val traceId = MDC.get("traceId") ?: "-"
        val spanId = MDC.get("spanId") ?: "-"

        val message =
            StringBuilder(
                "traceId=$traceId, spanId=$spanId, C=$className, M=${method.name}",
            )

        if (logInfo.logParameters) {
            message.append(", parameters=${buildParameters(method, args)}")
        }

        if (logInfo.logReturn && result !is Unit) {
            message.append(", return=$result")
        }

        return message.toString()
    }

    private fun buildParameters(
        method: Method,
        args: Array<Any?>,
    ): Map<String, Any?> {
        val annotatedParameters =
            method.parameters
                .mapIndexedNotNull { index, parameter ->
                    val annotation =
                        parameter.getAnnotation(LogParameter::class.java)
                            ?: return@mapIndexedNotNull null

                    val name = annotation.name.ifBlank { parameter.name }

                    name to args.getOrNull(index)
                }.toMap()

        if (annotatedParameters.isNotEmpty()) {
            return annotatedParameters
        }

        return args
            .mapIndexed { index, arg -> "arg$index" to arg }
            .toMap()
    }
}
