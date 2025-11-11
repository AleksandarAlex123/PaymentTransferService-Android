package com.aleksandar.paymenttransferservice_android.domain.transfer

import com.aleksandar.paymenttransferservice_android.core.AccountId
import com.aleksandar.paymenttransferservice_android.core.Money

data class TransferRequest(
    val sourceAccountId: AccountId,
    val destinationAccountId: AccountId,
    val amount: Money
)
