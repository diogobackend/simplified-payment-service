package com.simplifiedpayment.app.configuration.logs

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class LogInfo(
    val logParameters: Boolean = false,
    val logReturn: Boolean = false,
)
