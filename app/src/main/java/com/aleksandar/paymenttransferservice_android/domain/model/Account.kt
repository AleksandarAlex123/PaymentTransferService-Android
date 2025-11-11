package com.aleksandar.paymenttransferservice_android.domain.model

import com.aleksandar.paymenttransferservice_android.core.AccountId
import com.aleksandar.paymenttransferservice_android.core.Money

data class Account(
    val id: AccountId,
    val fullName: String,
    val accountNumber: String,
    val balance: Money
)
