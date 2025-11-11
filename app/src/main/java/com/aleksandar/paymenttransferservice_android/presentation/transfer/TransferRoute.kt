package com.aleksandar.paymenttransferservice_android.presentation.transfer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TransferRoute(
    modifier: Modifier = Modifier,
    viewModel: TransferViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    TransferScreen(
        state = state,
        onSourceChanged = viewModel::onSourceAccountChanged,
        onDestinationChanged = viewModel::onDestinationAccountChanged,
        onAmountChanged = viewModel::onAmountChanged,
        onTransferClick = viewModel::onTransferClick,
        modifier = modifier
    )
}

