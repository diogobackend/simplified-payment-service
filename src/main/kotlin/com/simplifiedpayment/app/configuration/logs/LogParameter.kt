package com.simplifiedpayment.app.configuration.logs

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class LogParameter(
    val name: String = "",
)
