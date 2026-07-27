package com.simplifiedpayment.app.adapter.input.web.mappers

import com.simplifiedpayment.app.adapter.input.web.requests.TransferRequest
import com.simplifiedpayment.app.adapter.input.web.responses.TransferResponse
import com.simplifiedpayment.core.domain.model.Transfer
import com.simplifiedpayment.core.port.input.TransferMoneyInput
import org.springframework.stereotype.Component

@Component
class TransferWebMapper {
    fun toInput(request: TransferRequest): TransferMoneyInput =
        TransferMoneyInput(
            value = request.value,
            payer = request.payer,
            payee = request.payee,
        )

    fun toResponse(transfer: Transfer): TransferResponse =
        TransferResponse(
            transferId = transfer.transferId.toString(),
            payer = transfer.payerId,
            payee = transfer.payeeId,
            value = transfer.value.value,
            status = transfer.status.name,
            createdAt = transfer.createdAt,
        )
}
