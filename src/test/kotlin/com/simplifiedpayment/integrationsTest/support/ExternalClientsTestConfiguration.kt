package com.simplifiedpayment.integrationsTest.support

import com.simplifiedpayment.core.domain.model.Transfer
import com.simplifiedpayment.core.port.output.AuthorizeTransferPort
import com.simplifiedpayment.core.port.output.NotifyPayeePort
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class ExternalClientsTestConfiguration {
    @Bean
    @Primary
    fun testAuthorizeTransferPort(): TestAuthorizeTransferPort = TestAuthorizeTransferPort()

    @Bean
    @Primary
    fun testNotifyPayeePort(): TestNotifyPayeePort = TestNotifyPayeePort()
}

class TestAuthorizeTransferPort : AuthorizeTransferPort {
    private var authorized = true

    fun authorizeTransfers() {
        authorized = true
    }

    fun denyTransfers() {
        authorized = false
    }

    override fun authorize(transfer: Transfer): Boolean = authorized
}

class TestNotifyPayeePort : NotifyPayeePort {
    private val notifiedTransfers = mutableListOf<Transfer>()

    override fun notify(transfer: Transfer) {
        notifiedTransfers.add(transfer)
    }

    fun reset() {
        notifiedTransfers.clear()
    }

    fun assertNotificationWasSent() {
        assertThat(notifiedTransfers).hasSize(1)
    }

    fun assertNotificationWasNotSent() {
        assertThat(notifiedTransfers).isEmpty()
    }
}
