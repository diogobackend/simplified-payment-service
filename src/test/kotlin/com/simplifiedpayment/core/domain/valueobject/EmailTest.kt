package com.simplifiedpayment.core.domain.valueobject

import com.simplifiedpayment.UnitTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class EmailTest : UnitTest() {
    @Test
    fun `should create email successfully`() {
        val email = Email(VALID_EMAIL)

        assertThat(email.value).isEqualTo(VALID_EMAIL)
    }

    @Test
    fun `should throw exception when email is blank`() {
        assertThatThrownBy {
            Email(" ")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should throw exception when email is invalid`() {
        assertThatThrownBy {
            Email("invalid-email")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    companion object {
        private const val VALID_EMAIL = "diogobackend@test.com"
    }
}
