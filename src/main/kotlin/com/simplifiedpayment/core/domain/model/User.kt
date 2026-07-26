package com.simplifiedpayment.core.domain.model

import com.simplifiedpayment.core.common.messages.PaymentMessages.USER_FULL_NAME_MUST_NOT_BE_BLANK
import com.simplifiedpayment.core.domain.valueobject.Document
import com.simplifiedpayment.core.domain.valueobject.Email
import com.simplifiedpayment.core.domain.valueobject.Password
import java.time.LocalDateTime

data class User(
    val userId: Long? = null,
    val fullName: String,
    val document: Document,
    val email: Email,
    val password: Password,
    val type: UserType,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null,
) {
    init {
        require(fullName.isNotBlank()) {
            USER_FULL_NAME_MUST_NOT_BE_BLANK
        }
    }

    fun isMerchant(): Boolean = type == UserType.MERCHANT

    fun canSendMoney(): Boolean = type == UserType.COMMON
}
