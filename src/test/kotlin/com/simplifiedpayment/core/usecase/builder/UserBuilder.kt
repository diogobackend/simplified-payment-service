package com.simplifiedpayment.core.usecase.builder

import com.simplifiedpayment.core.domain.model.User
import com.simplifiedpayment.core.domain.model.UserType
import com.simplifiedpayment.core.domain.model.UserType.COMMON
import com.simplifiedpayment.core.domain.valueobject.Document
import com.simplifiedpayment.core.domain.valueobject.Email
import com.simplifiedpayment.core.domain.valueobject.Password

fun buildUserDomain(
    userId: Long = 1L,
    fullName: String = "Diogo Ferreira",
    document: String = "12345678901",
    email: String = "user$userId@test.com",
    password: String = "123456",
    type: UserType = COMMON,
): User =
    User(
        userId = userId,
        fullName = fullName,
        document = Document(document),
        email = Email(email),
        password = Password(password),
        type = type,
    )
