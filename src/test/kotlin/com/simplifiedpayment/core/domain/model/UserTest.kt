package com.simplifiedpayment.core.domain.model

import com.simplifiedpayment.UnitTest
import com.simplifiedpayment.core.builder.buildUserDomain
import com.simplifiedpayment.core.builder.buildUserDomainWithDefaultId
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class UserTest : UnitTest() {
    @Test
    fun `should create common user successfully`() {
        val user = buildUserDomain(type = UserType.COMMON)

        assertThat(user.fullName).isEqualTo(FULL_NAME)
        assertThat(user.type).isEqualTo(UserType.COMMON)
        assertThat(user.canSendMoney()).isTrue()
        assertThat(user.isMerchant()).isFalse()
    }

    @Test
    fun `should create merchant user successfully`() {
        val user = buildUserDomain(type = UserType.MERCHANT)

        assertThat(user.fullName).isEqualTo(FULL_NAME)
        assertThat(user.type).isEqualTo(UserType.MERCHANT)
        assertThat(user.canSendMoney()).isFalse()
        assertThat(user.isMerchant()).isTrue()
    }

    @Test
    fun `should throw exception when full name is blank`() {
        assertThatThrownBy {
            buildUserDomain(fullName = " ")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should expose all user properties`() {
        val user =
            buildUserDomain(
                userId = USER_ID,
                fullName = FULL_NAME,
                document = DOCUMENT,
                email = EMAIL,
                password = PASSWORD,
                type = UserType.COMMON,
            )

        assertThat(user.userId).isEqualTo(USER_ID)
        assertThat(user.fullName).isEqualTo(FULL_NAME)
        assertThat(user.document.value).isEqualTo(DOCUMENT)
        assertThat(user.email.value).isEqualTo(EMAIL)
        assertThat(user.password.value).isEqualTo(PASSWORD)
        assertThat(user.type).isEqualTo(UserType.COMMON)
        assertThat(user.createdAt).isNotNull()
        assertThat(user.updatedAt).isNull()
    }

    @Test
    fun `should create user using default user id`() {
        val user = buildUserDomainWithDefaultId()

        assertThat(user.userId).isNull()
        assertThat(user.fullName).isEqualTo(FULL_NAME)
        assertThat(user.createdAt).isNotNull()
        assertThat(user.updatedAt).isNull()
    }

    companion object {
        private const val FULL_NAME = "Diogo Ferreira"
        private const val USER_ID = 1L
        private const val DOCUMENT = "12345678901"
        private const val EMAIL = "user@test.com"
        private const val PASSWORD = "123456"
    }
}
