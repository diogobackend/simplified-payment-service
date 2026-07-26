package com.simplifiedpayment.core.domain.model

import com.simplifiedpayment.core.common.messages.PaymentMessages.WALLET_BALANCE_INSUFFICIENT
import com.simplifiedpayment.core.common.messages.PaymentMessages.WALLET_USER_ID_MUST_BE_POSITIVE
import com.simplifiedpayment.core.domain.valueobject.Money
import java.time.LocalDateTime

data class Wallet(
    val walletId: Long? = null,
    val userId: Long,
    val balance: Money = Money.ZERO,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null,
) {
    init {
        require(userId > 0) {
            WALLET_USER_ID_MUST_BE_POSITIVE
        }
    }

    fun credit(amount: Money): Wallet =
        copy(
            balance = balance + amount,
            updatedAt = LocalDateTime.now(),
        )

    fun debit(amount: Money): Wallet {
        require(balance.isGreaterThanOrEqualTo(amount)) {
            WALLET_BALANCE_INSUFFICIENT
        }

        return copy(
            balance = balance - amount,
            updatedAt = LocalDateTime.now(),
        )
    }
}
