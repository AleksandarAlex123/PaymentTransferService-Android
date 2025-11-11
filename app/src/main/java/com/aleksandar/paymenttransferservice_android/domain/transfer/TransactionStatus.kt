package com.aleksandar.paymenttransferservice_android.domain.transfer

sealed interface TransactionStatus {
    data object Idle : TransactionStatus
    data object Loading : TransactionStatus
    data object Success : TransactionStatus
    data class Failed(val message: String) : TransactionStatus
}
