package com.simplifiedpayment

import com.simplifiedpayment.integrationsTest.support.ExternalClientsTestConfiguration
import com.simplifiedpayment.integrationsTest.support.PaymentDatabaseSupport
import com.simplifiedpayment.integrationsTest.support.TestAuthorizeTransferPort
import com.simplifiedpayment.integrationsTest.support.TestNotifyPayeePort
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureMockMvc
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "management.otlp.metrics.export.enabled=false",
    ],
)
@Import(ExternalClientsTestConfiguration::class)
abstract class IntegrationTest {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    protected lateinit var authorizeTransferPort: TestAuthorizeTransferPort

    @Autowired
    protected lateinit var notifyPayeePort: TestNotifyPayeePort

    protected val database by lazy {
        PaymentDatabaseSupport(jdbcTemplate)
    }

    @BeforeEach
    fun resetExternalClients() {
        authorizeTransferPort.authorizeTransfers()
        notifyPayeePort.reset()
    }
}
