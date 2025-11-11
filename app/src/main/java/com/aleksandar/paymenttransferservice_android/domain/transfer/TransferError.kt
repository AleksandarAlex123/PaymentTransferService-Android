package com.aleksandar.paymenttransferservice_android.domain.transfer

sealed interface TransferError {
    data object SourceNotFound : TransferError
    data object DestinationNotFound : TransferError
    data object SameAccount : TransferError
    data object InvalidAmount : TransferError
    data object InsufficientFunds : TransferError
    data object Technical : TransferError
}
