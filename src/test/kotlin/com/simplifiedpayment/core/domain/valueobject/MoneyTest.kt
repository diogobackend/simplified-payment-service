package com.simplifiedpayment.core.domain.valueobject

import com.simplifiedpayment.UnitTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class MoneyTest : UnitTest() {
    @Test
    fun `should create money successfully`() {
        val money = Money(BigDecimal("100.00"))

        assertThat(money.value).isEqualByComparingTo(BigDecimal("100.00"))
    }

    @Test
    fun `should throw exception when value is negative`() {
        assertThatThrownBy {
            Money(BigDecimal("-1.00"))
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should add money values`() {
        val firstMoney = Money(BigDecimal("100.00"))
        val secondMoney = Money(BigDecimal("50.00"))

        val result = firstMoney + secondMoney

        assertThat(result.value).isEqualByComparingTo(BigDecimal("150.00"))
    }

    @Test
    fun `should subtract money values`() {
        val firstMoney = Money(BigDecimal("100.00"))
        val secondMoney = Money(BigDecimal("50.00"))

        val result = firstMoney - secondMoney

        assertThat(result.value).isEqualByComparingTo(BigDecimal("50.00"))
    }

    @Test
    fun `should return true when money is less than another money`() {
        val firstMoney = Money(BigDecimal("50.00"))
        val secondMoney = Money(BigDecimal("100.00"))

        val result = firstMoney.isLessThan(secondMoney)

        assertThat(result).isTrue()
    }

    @Test
    fun `should return true when money is greater than or equal to another money`() {
        val firstMoney = Money(BigDecimal("100.00"))
        val secondMoney = Money(BigDecimal("100.00"))

        val result = firstMoney.isGreaterThanOrEqualTo(secondMoney)

        assertThat(result).isTrue()
    }

    @Test
    fun `should return true when money is greater than another money`() {
        val firstMoney = Money(BigDecimal("100.00"))
        val secondMoney = Money(BigDecimal("50.00"))

        val result = firstMoney.isGreaterThan(secondMoney)

        assertThat(result).isTrue()
    }

    @Test
    fun `should return false when money is not less than another money`() {
        val firstMoney = Money(BigDecimal("100.00"))
        val secondMoney = Money(BigDecimal("50.00"))

        val result = firstMoney.isLessThan(secondMoney)

        assertThat(result).isFalse()
    }

    @Test
    fun `should return false when money is not greater than another money`() {
        val firstMoney = Money(BigDecimal("50.00"))
        val secondMoney = Money(BigDecimal("100.00"))

        val result = firstMoney.isGreaterThan(secondMoney)

        assertThat(result).isFalse()
    }

    @Test
    fun `should return false when money is less than another money on greater than or equal comparison`() {
        val firstMoney = Money(BigDecimal("50.00"))
        val secondMoney = Money(BigDecimal("100.00"))

        val result = firstMoney.isGreaterThanOrEqualTo(secondMoney)

        assertThat(result).isFalse()
    }
}
