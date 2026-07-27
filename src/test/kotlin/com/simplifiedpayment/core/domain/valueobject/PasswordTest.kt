package com.simplifiedpayment.core.domain.valueobject

import com.simplifiedpayment.UnitTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class PasswordTest : UnitTest() {
    @Test
    fun `should create password successfully`() {
        val password = Password(VALID_PASSWORD)

        assertThat(password.value).isEqualTo(VALID_PASSWORD)
    }

    @Test
    fun `should throw exception when password is blank`() {
        assertThatThrownBy {
            Password(" ")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should throw exception when password has less than minimum length`() {
        assertThatThrownBy {
            Password("12345")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    companion object {
        private const val VALID_PASSWORD = "123456"
    }
}
