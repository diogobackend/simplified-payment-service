package com.simplifiedpayment.core.domain.valueobject

import com.simplifiedpayment.UnitTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class DocumentTest : UnitTest() {
    @Test
    fun `should create document with cpf successfully`() {
        val document = Document(CPF)

        assertThat(document.value).isEqualTo(CPF)
    }

    @Test
    fun `should create document with cnpj successfully`() {
        val document = Document(CNPJ)

        assertThat(document.value).isEqualTo(CNPJ)
    }

    @Test
    fun `should throw exception when document is blank`() {
        assertThatThrownBy {
            Document(" ")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should throw exception when document contains letters`() {
        assertThatThrownBy {
            Document("1234567890A")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should throw exception when document has invalid length`() {
        assertThatThrownBy {
            Document("12345")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    companion object {
        private const val CPF = "12345678901"
        private const val CNPJ = "12345678000199"
    }
}
