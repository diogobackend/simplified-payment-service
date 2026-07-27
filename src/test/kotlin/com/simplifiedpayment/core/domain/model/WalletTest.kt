package com.simplifiedpayment.core.domain.model

import com.simplifiedpayment.UnitTest
import com.simplifiedpayment.core.builder.buildWalletDomain
import com.simplifiedpayment.core.builder.buildWalletDomainWithDefaults
import com.simplifiedpayment.core.domain.valueobject.Money
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class WalletTest : UnitTest() {
    @Test
    fun `should create wallet successfully`() {
        val wallet = buildWalletDomain()

        assertThat(wallet.userId).isEqualTo(USER_ID)
        assertThat(wallet.balance.value).isEqualByComparingTo(INITIAL_BALANCE)
    }

    @Test
    fun `should throw exception when user id is invalid`() {
        assertThatThrownBy {
            buildWalletDomain(userId = 0L)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should credit wallet balance`() {
        val wallet = buildWalletDomain()

        val result = wallet.credit(Money(TRANSFER_VALUE))

        assertThat(result.balance.value).isEqualByComparingTo(BALANCE_AFTER_CREDIT)
    }

    @Test
    fun `should debit wallet balance`() {
        val wallet = buildWalletDomain()

        val result = wallet.debit(Money(TRANSFER_VALUE))

        assertThat(result.balance.value).isEqualByComparingTo(BALANCE_AFTER_DEBIT)
    }

    @Test
    fun `should throw exception when debit value is greater than balance`() {
        val wallet = buildWalletDomain(balance = BigDecimal("50.00"))

        assertThatThrownBy {
            wallet.debit(Money(TRANSFER_VALUE))
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should create wallet using default values`() {
        val wallet = buildWalletDomainWithDefaults(userId = USER_ID)

        assertThat(wallet.walletId).isNull()
        assertThat(wallet.userId).isEqualTo(USER_ID)
        assertThat(wallet.balance.value).isEqualByComparingTo(BigDecimal.ZERO)
        assertThat(wallet.createdAt).isNotNull()
        assertThat(wallet.updatedAt).isNull()
    }

    companion object {
        private const val USER_ID = 1L

        private val INITIAL_BALANCE = BigDecimal("200.00")
        private val TRANSFER_VALUE = BigDecimal("100.00")
        private val BALANCE_AFTER_CREDIT = BigDecimal("300.00")
        private val BALANCE_AFTER_DEBIT = BigDecimal("100.00")
    }
}
