package com.aleksandar.paymenttransferservice_android.domain.model

import com.aleksandar.paymenttransferservice_android.core.AccountId
import com.aleksandar.paymenttransferservice_android.core.Money
import java.time.Instant
import java.util.UUID

data class Transaction(
    val id: String,
    val sourceAccountId: AccountId,
    val destinationAccountId: AccountId,
    val amount: Money,
    val createdAt: Instant
) {
    companion object {
        fun create(
            sourceAccountId: AccountId,
            destinationAccountId: AccountId,
            amount: Money,
            at: Instant
        ): Transaction = Transaction(
            id = UUID.randomUUID().toString(),
            sourceAccountId = sourceAccountId,
            destinationAccountId = destinationAccountId,
            amount = amount,
            createdAt = at
        )
    }
}
