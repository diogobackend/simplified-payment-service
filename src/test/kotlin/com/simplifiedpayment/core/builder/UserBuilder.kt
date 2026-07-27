package com.simplifiedpayment.core.builder

import com.simplifiedpayment.core.domain.model.User
import com.simplifiedpayment.core.domain.model.UserType
import com.simplifiedpayment.core.domain.model.UserType.COMMON
import com.simplifiedpayment.core.domain.valueobject.Document
import com.simplifiedpayment.core.domain.valueobject.Email
import com.simplifiedpayment.core.domain.valueobject.Password

fun buildUserDomain(
    userId: Long = 1L,
    fullName: String = DEFAULT_FULL_NAME,
    document: String = DEFAULT_DOCUMENT,
    email: String = "user$userId@test.com",
    password: String = DEFAULT_PASSWORD,
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

fun buildUserDomainWithDefaultId(
    fullName: String = DEFAULT_FULL_NAME,
    document: String = DEFAULT_DOCUMENT,
    email: String = DEFAULT_EMAIL,
    password: String = DEFAULT_PASSWORD,
    type: UserType = COMMON,
): User =
    User(
        fullName = fullName,
        document = Document(document),
        email = Email(email),
        password = Password(password),
        type = type,
    )

private const val DEFAULT_FULL_NAME = "Diogo Ferreira"
private const val DEFAULT_DOCUMENT = "12345678901"
private const val DEFAULT_EMAIL = "user@test.com"
private const val DEFAULT_PASSWORD = "123456"
