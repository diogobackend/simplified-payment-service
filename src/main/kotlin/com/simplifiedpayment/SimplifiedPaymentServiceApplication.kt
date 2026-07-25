package com.simplifiedpayment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SimplifiedPaymentServiceApplication

fun main(args: Array<String>) {
	runApplication<SimplifiedPaymentServiceApplication>(*args)
}
