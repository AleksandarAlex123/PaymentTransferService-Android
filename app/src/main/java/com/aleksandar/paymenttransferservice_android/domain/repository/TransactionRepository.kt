package com.aleksandar.paymenttransferservice_android.domain.repository

import com.aleksandar.paymenttransferservice_android.domain.model.Transaction


interface TransactionRepository {
    suspend fun insert(transaction: Transaction)
    suspend fun getAll(): List<Transaction>
}
