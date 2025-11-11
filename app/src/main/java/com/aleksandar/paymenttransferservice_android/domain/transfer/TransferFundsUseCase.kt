package com.aleksandar.paymenttransferservice_android.domain.transfer

import com.aleksandar.paymenttransferservice_android.core.Money
import com.aleksandar.paymenttransferservice_android.domain.model.Account
import com.aleksandar.paymenttransferservice_android.domain.model.Transaction
import com.aleksandar.paymenttransferservice_android.domain.repository.AccountRepository
import com.aleksandar.paymenttransferservice_android.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.Instant

class TransferFundsUseCase(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val clock: Clock = Clock.systemUTC()
) {

    // Simulates atomicity for concurrent operations (thread-safe section)
    private val mutex = Mutex()

    /**
     * Executes a fund transfer between two accounts.
     *
     * @param request contains source, destination, and amount data
     * @return [TransferResult] representing success or failure of the operation
     */
    suspend operator fun invoke(request: TransferRequest): TransferResult =
        withContext(dispatcher) {
            // 1) Basic validation
            if (request.sourceAccountId == request.destinationAccountId) {
                return@withContext TransferResult.Failure(TransferError.SameAccount)
            }

            if (request.amount.isNonPositive()) {
                return@withContext TransferResult.Failure(TransferError.InvalidAmount)
            }

            runCatching {
                mutex.withLock {
                    // 2) Load both accounts
                    val source = accountRepository.getById(request.sourceAccountId)
                        ?: return@withLock TransferResult.Failure(TransferError.SourceNotFound)

                    val destination = accountRepository.getById(request.destinationAccountId)
                        ?: return@withLock TransferResult.Failure(TransferError.DestinationNotFound)

                    // 3) Check available funds
                    if (source.balance < request.amount) {
                        return@withLock TransferResult.Failure(TransferError.InsufficientFunds)
                    }

                    // 4) Calculate new balances
                    val updatedSource = source.debit(request.amount)
                    val updatedDestination = destination.credit(request.amount)

                    // 5) Persist updated accounts
                    accountRepository.update(updatedSource)
                    accountRepository.update(updatedDestination)

                    // 6) Log transaction for audit trail
                    val transaction = Transaction.create(
                        sourceAccountId = source.id,
                        destinationAccountId = destination.id,
                        amount = request.amount,
                        at = Instant.now(clock)
                    )

                    transactionRepository.insert(transaction)

                    TransferResult.Success(transaction)
                }
            }.getOrElse {
                // In production, exceptions should be logged via Timber / Crashlytics
                TransferResult.Failure(TransferError.Technical)
            }
        }

    // Helper extensions to modify account balances immutably
    private fun Account.debit(amount: Money): Account =
        copy(balance = balance - amount)

    private fun Account.credit(amount: Money): Account =
        copy(balance = balance + amount)
}
