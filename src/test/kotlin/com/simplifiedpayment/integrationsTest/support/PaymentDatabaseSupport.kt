package com.simplifiedpayment.integrationsTest.support

import org.assertj.core.api.Assertions.assertThat
import org.springframework.jdbc.core.JdbcTemplate
import java.math.BigDecimal

class PaymentDatabaseSupport(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun deleteTransfers() {
        jdbcTemplate.update("DELETE FROM transfers")
    }

    fun updateWalletBalance(
        userId: Long,
        balance: BigDecimal,
    ) {
        jdbcTemplate.update(
            """
            UPDATE wallets
            SET balance = ?
            WHERE user_id = ?
            """.trimIndent(),
            balance,
            userId,
        )
    }

    fun assertWalletBalance(
        userId: Long,
        expectedBalance: BigDecimal,
    ) {
        val balance =
            jdbcTemplate.queryForObject(
                """
                SELECT balance
                FROM wallets
                WHERE user_id = ?
                """.trimIndent(),
                BigDecimal::class.java,
                userId,
            )

        assertThat(balance).isEqualByComparingTo(expectedBalance)
    }

    fun assertCompletedTransferExists(
        payerId: Long,
        payeeId: Long,
    ) {
        val count =
            jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM transfers
                WHERE payer_id = ?
                  AND payee_id = ?
                  AND status = 'COMPLETED'
                """.trimIndent(),
                Int::class.java,
                payerId,
                payeeId,
            )

        assertThat(count).isEqualTo(1)
    }

    fun assertTransferCount(expectedCount: Int) {
        val count =
            jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM transfers",
                Int::class.java,
            )

        assertThat(count).isEqualTo(expectedCount)
    }
}
