package com.aleksandar.paymenttransferservice_android.domain.transfer

import com.aleksandar.paymenttransferservice_android.domain.model.Transaction

sealed interface TransferResult {
    data class Success(val transaction: Transaction) : TransferResult
    data class Failure(val error: TransferError) : TransferResult
}
