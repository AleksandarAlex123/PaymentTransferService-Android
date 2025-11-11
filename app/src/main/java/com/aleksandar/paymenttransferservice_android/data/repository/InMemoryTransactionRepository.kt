package com.aleksandar.paymenttransferservice_android.data.repository

import com.aleksandar.paymenttransferservice_android.domain.model.Transaction
import com.aleksandar.paymenttransferservice_android.domain.repository.TransactionRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Keeps all transactions in a local list for demo or testing purposes.
 * Uses a [Mutex] to guarantee thread-safety when multiple coroutines
 * are inserting or reading transactions concurrently.
 */
class InMemoryTransactionRepository : TransactionRepository {

    private val transactions = mutableListOf<Transaction>()
    private val mutex = Mutex()

    override suspend fun insert(transaction: Transaction): Unit = mutex.withLock {
        transactions.add(transaction)
    }

    override suspend fun getAll(): List<Transaction> = mutex.withLock {
        transactions.toList()
    }
}
