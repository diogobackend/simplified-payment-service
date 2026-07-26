package com.simplifiedpayment.core.domain.valueobject

import com.simplifiedpayment.core.common.messages.PaymentMessages.DOCUMENT_MUST_CONTAIN_ONLY_DIGITS
import com.simplifiedpayment.core.common.messages.PaymentMessages.DOCUMENT_MUST_HAVE_VALID_LENGTH
import com.simplifiedpayment.core.common.messages.PaymentMessages.DOCUMENT_MUST_NOT_BE_BLANK

data class Document(
    val value: String,
) {
    init {
        require(value.isNotBlank()) {
            DOCUMENT_MUST_NOT_BE_BLANK
        }

        require(value.all { it.isDigit() }) {
            DOCUMENT_MUST_CONTAIN_ONLY_DIGITS
        }

        require(value.length == CPF_LENGTH || value.length == CNPJ_LENGTH) {
            DOCUMENT_MUST_HAVE_VALID_LENGTH
        }
    }

    companion object {
        private const val CPF_LENGTH = 11
        private const val CNPJ_LENGTH = 14
    }
}
