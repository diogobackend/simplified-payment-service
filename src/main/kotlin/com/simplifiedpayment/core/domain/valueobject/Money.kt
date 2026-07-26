package com.simplifiedpayment.core.domain.valueobject

import com.simplifiedpayment.core.common.messages.PaymentMessages.MONEY_MUST_NOT_BE_NEGATIVE
import java.math.BigDecimal

data class Money(
    val value: BigDecimal,
) {
    init {
        require(value >= BigDecimal.ZERO) {
            MONEY_MUST_NOT_BE_NEGATIVE
        }
    }

    operator fun plus(money: Money): Money = Money(value.add(money.value))

    operator fun minus(money: Money): Money = Money(value.subtract(money.value))

    fun isLessThan(money: Money): Boolean = value < money.value

    fun isGreaterThanOrEqualTo(money: Money): Boolean = value >= money.value

    fun isGreaterThan(money: Money): Boolean = value > money.value

    companion object {
        val ZERO = Money(BigDecimal.ZERO)
    }
}
