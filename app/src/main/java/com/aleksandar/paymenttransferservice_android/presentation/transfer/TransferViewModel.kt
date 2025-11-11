package com.aleksandar.paymenttransferservice_android.presentation.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleksandar.paymenttransferservice_android.R
import com.aleksandar.paymenttransferservice_android.core.AccountId
import com.aleksandar.paymenttransferservice_android.core.Money
import com.aleksandar.paymenttransferservice_android.core.util.ValidationHelper
import com.aleksandar.paymenttransferservice_android.domain.repository.AccountRepository
import com.aleksandar.paymenttransferservice_android.domain.repository.TransactionRepository
import com.aleksandar.paymenttransferservice_android.domain.transfer.TransferError
import com.aleksandar.paymenttransferservice_android.domain.transfer.TransferFundsUseCase
import com.aleksandar.paymenttransferservice_android.domain.transfer.TransferRequest
import com.aleksandar.paymenttransferservice_android.domain.transfer.TransferResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val transferFundsUseCase: TransferFundsUseCase,
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    fun onSourceAccountChanged(value: String) {
        val trimmed = value.trim()

        // reset field + clear previous errors and balance
        _uiState.value = _uiState.value.copy(
            sourceAccountId = value,
            sourceErrorResId = null,
            sourceAccountBalance = null,
            isSuccess = false
        )

        // If the input looks valid, try to resolve account and show its balance.
        if (ValidationHelper.isValidAccountId(trimmed)) {
            viewModelScope.launch {
                val account = accountRepository.getById(AccountId(trimmed))
                _uiState.value = _uiState.value.copy(
                    sourceAccountBalance = account?.balance?.toString()
                )
            }
        }
    }

    fun onDestinationAccountChanged(value: String) {
        _uiState.value = _uiState.value.copy(
            destinationAccountId = value,
            destinationErrorResId = null,
            isSuccess = false
        )
    }

    fun onAmountChanged(value: String) {
        _uiState.value = _uiState.value.copy(
            amount = value,
            amountErrorResId = null,
            isSuccess = false
        )
    }

    fun onTransferClick() {
        val current = _uiState.value

        val sourceId = current.sourceAccountId.trim()
        val destinationId = current.destinationAccountId.trim()
        val amountInput = current.amount.trim()

        val sourceErrorRes = if (!ValidationHelper.isValidAccountId(sourceId)) {
            R.string.error_source_invalid_format
        } else null

        val destinationErrorRes = if (!ValidationHelper.isValidAccountId(destinationId)) {
            R.string.error_destination_invalid_format
        } else null

        val amountErrorRes = if (!ValidationHelper.isValidAmount(amountInput)) {
            R.string.error_amount_invalid_format
        } else null

        if (sourceErrorRes != null || destinationErrorRes != null || amountErrorRes != null) {
            _uiState.value = current.copy(
                sourceErrorResId = sourceErrorRes,
                destinationErrorResId = destinationErrorRes,
                amountErrorResId = amountErrorRes,
                isLoading = false,
                isSuccess = false,
                errorType = null,
                messageResId = R.string.hint_check_fields,
                messageId = current.messageId + 1
            )
            return
        }

        if (sourceId == destinationId) {
            _uiState.value = current.copy(
                isLoading = false,
                isSuccess = false,
                errorType = TransferError.SameAccount,
                messageResId = null,
                messageId = current.messageId + 1
            )
            return
        }

        val money = try {
            Money.from(amountInput)
        } catch (_: Exception) {
            _uiState.value = current.copy(
                amountErrorResId = R.string.error_amount_invalid_value,
                isLoading = false,
                isSuccess = false,
                errorType = null,
                messageResId = R.string.hint_check_amount,
                messageId = current.messageId + 1
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                isSuccess = false,
                errorType = null,
                messageResId = null
            )

            val request = TransferRequest(
                sourceAccountId = AccountId(sourceId),
                destinationAccountId = AccountId(destinationId),
                amount = money
            )

            when (val result = transferFundsUseCase(request)) {
                is TransferResult.Success -> {
                    val updatedTransactions = transactionRepository.getAll()
                    val currentId = _uiState.value.messageId

                    _uiState.value = TransferUiState(
                        sourceAccountId = "",
                        destinationAccountId = "",
                        amount = "",
                        isLoading = false,
                        isSuccess = true,
                        sourceErrorResId = null,
                        destinationErrorResId = null,
                        amountErrorResId = null,
                        errorType = null,
                        messageResId = R.string.success_transfer,
                        transactions = updatedTransactions,
                        messageId = currentId + 1
                    )
                }

                is TransferResult.Failure -> {
                    val prev = _uiState.value
                    _uiState.value = prev.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorType = result.error,
                        messageResId = null,
                        messageId = prev.messageId + 1
                    )
                }
            }
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            val all = transactionRepository.getAll()
            _uiState.value = _uiState.value.copy(transactions = all)
        }
    }
}
