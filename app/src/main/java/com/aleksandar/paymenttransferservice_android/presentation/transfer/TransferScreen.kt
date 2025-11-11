package com.aleksandar.paymenttransferservice_android.presentation.transfer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aleksandar.paymenttransferservice_android.R
import com.aleksandar.paymenttransferservice_android.domain.transfer.TransferError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    state: TransferUiState,
    onSourceChanged: (String) -> Unit,
    onDestinationChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onTransferClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Triggered whenever messageId changes.
    LaunchedEffect(state.messageId) {
        val messageText = when {
            state.messageResId != null ->
                context.getString(state.messageResId)

            state.errorType != null -> when (state.errorType) {
                TransferError.SourceNotFound ->
                    context.getString(R.string.error_source_not_found)

                TransferError.DestinationNotFound ->
                    context.getString(R.string.error_destination_not_found)

                TransferError.SameAccount ->
                    context.getString(R.string.error_same_account)

                TransferError.InvalidAmount ->
                    context.getString(R.string.error_invalid_amount)

                TransferError.InsufficientFunds ->
                    context.getString(R.string.error_insufficient_funds)

                TransferError.Technical ->
                    context.getString(R.string.error_technical)
            }

            else -> null
        }

        if (!messageText.isNullOrBlank()) {
            snackbarHostState.showSnackbar(messageText)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.internal_payment_transfer)) }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.sourceAccountBalance != null) {
                Text(
                    text = stringResource(R.string.available_balance, state.sourceAccountBalance),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Source account input
            OutlinedTextField(
                value = state.sourceAccountId,
                onValueChange = onSourceChanged,
                label = { Text(stringResource(R.string.source_account_id)) },
                isError = state.sourceErrorResId != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (state.sourceErrorResId != null) {
                Text(
                    text = context.getString(state.sourceErrorResId),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Destination account input
            OutlinedTextField(
                value = state.destinationAccountId,
                onValueChange = onDestinationChanged,
                label = { Text(stringResource(R.string.destination_account_id)) },
                isError = state.destinationErrorResId != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (state.destinationErrorResId != null) {
                Text(
                    text = context.getString(state.destinationErrorResId),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Amount input
            OutlinedTextField(
                value = state.amount,
                onValueChange = onAmountChanged,
                label = { Text(stringResource(R.string.amount)) },
                isError = state.amountErrorResId != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            if (state.amountErrorResId != null) {
                Text(
                    text = context.getString(state.amountErrorResId),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Transfer action button
            Button(
                onClick = {
                    focusManager.clearFocus(force = true)
                    keyboardController?.hide()
                    onTransferClick()
                },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                if (state.isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(stringResource(R.string.processing))
                    }
                } else {
                    Text(stringResource(R.string.transfer))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recent transactions list
            if (state.transactions.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.recent_transfers),
                    style = MaterialTheme.typography.titleMedium
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(state.transactions) { tx ->
                        TransactionRow(
                            source = tx.sourceAccountId.toString(),
                            destination = tx.destinationAccountId.toString(),
                            amount = tx.amount.toString()
                        )
                    }
                }
            }
        }
    }
}
