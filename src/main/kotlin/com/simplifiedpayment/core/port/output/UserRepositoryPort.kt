package com.simplifiedpayment.core.port.output

import com.simplifiedpayment.core.domain.model.User
import com.simplifiedpayment.core.domain.valueobject.Document
import com.simplifiedpayment.core.domain.valueobject.Email

interface UserRepositoryPort {
    fun save(user: User): User

    fun findById(userId: Long): User?

    fun existsByDocument(document: Document): Boolean

    fun existsByEmail(email: Email): Boolean
}
