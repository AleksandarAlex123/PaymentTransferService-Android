package com.aleksandar.paymenttransferservice_android.presentation.transfer

import androidx.annotation.StringRes
import com.aleksandar.paymenttransferservice_android.domain.model.Transaction
import com.aleksandar.paymenttransferservice_android.domain.transfer.TransferError

data class TransferUiState(
    val sourceAccountId: String = "",
    val destinationAccountId: String = "",
    val amount: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val transactions: List<Transaction> = emptyList(),

    // Field-level errors as @StringRes ids
    @StringRes val sourceErrorResId: Int? = null,
    @StringRes val destinationErrorResId: Int? = null,
    @StringRes val amountErrorResId: Int? = null,

    // High-level domain error (mapped to string in UI)
    val errorType: TransferError? = null,

    // Optional generic/info message as @StringRes (e.g. success, generic hints)
    @StringRes val messageResId: Int? = null,

    // Bump this to trigger one-off snackbar events.
    val messageId: Long = 0L
)
