package com.simplifiedpayment.app.adapter.input.web.controllers

import com.simplifiedpayment.app.adapter.input.web.mappers.TransferWebMapper
import com.simplifiedpayment.app.adapter.input.web.requests.TransferRequest
import com.simplifiedpayment.app.adapter.input.web.responses.TransferResponse
import com.simplifiedpayment.core.port.input.TransferMoneyPort
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/transfer")
class TransferController(
    private val transferMoneyPort: TransferMoneyPort,
    private val transferWebMapper: TransferWebMapper,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun transfer(
        @Valid
        @RequestBody
        request: TransferRequest,
    ): TransferResponse =
        transferMoneyPort
            .transfer(transferWebMapper.toInput(request))
            .let(transferWebMapper::toResponse)
}
