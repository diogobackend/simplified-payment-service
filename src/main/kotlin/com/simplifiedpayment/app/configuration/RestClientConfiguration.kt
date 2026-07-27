package com.simplifiedpayment.app.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfiguration {
    @Bean
    fun restClientBuilder(): RestClient.Builder = RestClient.builder()
}
