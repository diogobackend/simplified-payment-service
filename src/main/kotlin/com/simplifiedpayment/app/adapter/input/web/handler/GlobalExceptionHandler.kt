package com.simplifiedpayment.app.adapter.input.web.handler

import com.simplifiedpayment.core.common.messages.PaymentMessages.INVALID_REQUEST
import com.simplifiedpayment.core.common.messages.PaymentMessages.MERCHANT_CANNOT_SEND_MONEY
import com.simplifiedpayment.core.common.messages.PaymentMessages.RESOURCE_NOT_FOUND
import com.simplifiedpayment.core.common.messages.PaymentMessages.TRANSFER_NOT_AUTHORIZED
import com.simplifiedpayment.core.domain.exception.MerchantCannotSendMoneyException
import com.simplifiedpayment.core.domain.exception.TransferNotAuthorizedException
import com.simplifiedpayment.core.domain.exception.UserNotFoundException
import com.simplifiedpayment.core.domain.exception.WalletNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException::class, WalletNotFoundException::class)
    fun handleNotFound(exception: RuntimeException): ResponseEntity<ApiErrorResponse> =
        buildErrorResponse(
            status = NOT_FOUND,
            message = exception.message ?: RESOURCE_NOT_FOUND,
        )

    @ExceptionHandler(MerchantCannotSendMoneyException::class)
    fun handleMerchantCannotSendMoney(exception: MerchantCannotSendMoneyException): ResponseEntity<ApiErrorResponse> =
        buildErrorResponse(
            status = UNPROCESSABLE_CONTENT,
            message = exception.message ?: MERCHANT_CANNOT_SEND_MONEY,
        )

    @ExceptionHandler(TransferNotAuthorizedException::class)
    fun handleTransferNotAuthorized(exception: TransferNotAuthorizedException): ResponseEntity<ApiErrorResponse> =
        buildErrorResponse(
            status = FORBIDDEN,
            message = exception.message ?: TRANSFER_NOT_AUTHORIZED,
        )

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(exception: IllegalArgumentException): ResponseEntity<ApiErrorResponse> =
        buildErrorResponse(
            status = BAD_REQUEST,
            message = exception.message ?: INVALID_REQUEST,
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(exception: MethodArgumentNotValidException): ResponseEntity<ApiErrorResponse> {
        val message =
            exception
                .bindingResult
                .fieldErrors
                .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }

        return buildErrorResponse(
            status = BAD_REQUEST,
            message = message,
        )
    }

    private fun buildErrorResponse(
        status: HttpStatus,
        message: String,
    ): ResponseEntity<ApiErrorResponse> =
        ResponseEntity
            .status(status)
            .body(
                ApiErrorResponse(
                    status = status.value(),
                    error = status.reasonPhrase,
                    message = message,
                ),
            )
}
