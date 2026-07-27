package com.simplifiedpayment.core.builder

import com.simplifiedpayment.core.domain.model.Wallet
import com.simplifiedpayment.core.domain.valueobject.Money
import java.math.BigDecimal

fun buildWalletDomain(
    walletId: Long = 1L,
    userId: Long = 1L,
    balance: BigDecimal = BigDecimal("200.00"),
): Wallet =
    Wallet(
        walletId = walletId,
        userId = userId,
        balance = Money(balance),
    )

fun buildWalletDomainWithDefaults(userId: Long = 1L): Wallet =
    Wallet(
        userId = userId,
    )
