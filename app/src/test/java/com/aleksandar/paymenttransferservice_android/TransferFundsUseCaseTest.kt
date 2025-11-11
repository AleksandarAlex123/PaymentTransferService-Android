package com.aleksandar.paymenttransferservice_android

import com.aleksandar.paymenttransferservice_android.core.AccountId
import com.aleksandar.paymenttransferservice_android.core.Money
import com.aleksandar.paymenttransferservice_android.domain.model.Account
import com.aleksandar.paymenttransferservice_android.domain.model.Transaction
import com.aleksandar.paymenttransferservice_android.domain.repository.AccountRepository
import com.aleksandar.paymenttransferservice_android.domain.repository.TransactionRepository
import com.aleksandar.paymenttransferservice_android.domain.transfer.TransferError
import com.aleksandar.paymenttransferservice_android.domain.transfer.TransferFundsUseCase
import com.aleksandar.paymenttransferservice_android.domain.transfer.TransferRequest
import com.aleksandar.paymenttransferservice_android.domain.transfer.TransferResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

/**
 * Unit tests for TransferFundsUseCase
 *
 * Focus:
 * - correct balance updates
 * - error handling
 * - transaction recording
 */
class TransferFundsUseCaseTest {

    private val fixedClock: Clock = Clock.fixed(
        Instant.parse("2024-01-01T10:00:00Z"),
        ZoneOffset.UTC
    )

    private fun createUseCase(
        accountRepository: AccountRepository,
        transactionRepository: TransactionRepository
    ): TransferFundsUseCase =
        TransferFundsUseCase(
            accountRepository = accountRepository,
            transactionRepository = transactionRepository,
            clock = fixedClock
        )

    @Test
    fun `successful transfer updates balances and records transaction`() = runBlocking {
        val accountRepo = InMemoryAccountRepoFake(
            accounts = listOf(
                account("ACC-001", 500.00),
                account("ACC-002", 100.00)
            )
        )
        val txRepo = InMemoryTransactionRepoFake()
        val useCase = createUseCase(accountRepo, txRepo)

        val result = useCase(
            TransferRequest(
                sourceAccountId = AccountId("ACC-001"),
                destinationAccountId = AccountId("ACC-002"),
                amount = Money.from(50.00)
            )
        )

        assertTrue(result is TransferResult.Success)

        val updatedSource = accountRepo.getById(AccountId("ACC-001"))!!
        val updatedDest = accountRepo.getById(AccountId("ACC-002"))!!
        val transactions = txRepo.getAll()

        assertEquals(Money.from(450.00), updatedSource.balance)
        assertEquals(Money.from(150.00), updatedDest.balance)
        assertEquals(1, transactions.size)
        assertEquals(Money.from(50.00), transactions.first().amount)
    }

    @Test
    fun `insufficient funds returns error and does not change balances`() = runBlocking {
        val accountRepo = InMemoryAccountRepoFake(
            accounts = listOf(
                account("ACC-001", 30.00),
                account("ACC-002", 100.00)
            )
        )
        val txRepo = InMemoryTransactionRepoFake()
        val useCase = createUseCase(accountRepo, txRepo)

        val result = useCase(
            TransferRequest(
                sourceAccountId = AccountId("ACC-001"),
                destinationAccountId = AccountId("ACC-002"),
                amount = Money.from(50.00)
            )
        )

        assertTrue(result is TransferResult.Failure)
        assertEquals(TransferError.InsufficientFunds, (result as TransferResult.Failure).error)

        // Balances unchanged
        assertEquals(Money.from(30.00), accountRepo.getById(AccountId("ACC-001"))!!.balance)
        assertEquals(Money.from(100.00), accountRepo.getById(AccountId("ACC-002"))!!.balance)
        assertTrue(txRepo.getAll().isEmpty())
    }

    @Test
    fun `source account not found returns proper error`() = runBlocking {
        val accountRepo = InMemoryAccountRepoFake(
            accounts = listOf(
                account("ACC-002", 100.00)
            )
        )
        val txRepo = InMemoryTransactionRepoFake()
        val useCase = createUseCase(accountRepo, txRepo)

        val result = useCase(
            TransferRequest(
                sourceAccountId = AccountId("ACC-X"),
                destinationAccountId = AccountId("ACC-002"),
                amount = Money.from(10.00)
            )
        )

        assertTrue(result is TransferResult.Failure)
        assertEquals(TransferError.SourceNotFound, (result as TransferResult.Failure).error)
        assertTrue(txRepo.getAll().isEmpty())
    }

    @Test
    fun `same account transfer is rejected`() = runBlocking {
        val accountRepo = InMemoryAccountRepoFake(
            accounts = listOf(
                account("ACC-001", 500.00)
            )
        )
        val txRepo = InMemoryTransactionRepoFake()
        val useCase = createUseCase(accountRepo, txRepo)

        val result = useCase(
            TransferRequest(
                sourceAccountId = AccountId("ACC-001"),
                destinationAccountId = AccountId("ACC-001"),
                amount = Money.from(10.00)
            )
        )

        assertTrue(result is TransferResult.Failure)
        assertEquals(TransferError.SameAccount, (result as TransferResult.Failure).error)
        assertTrue(txRepo.getAll().isEmpty())
    }

    // --- Test helpers & fakes ---

    private fun account(id: String, balance: Double): Account =
        Account(
            id = AccountId(id),
            fullName = "Test User $id",
            accountNumber = "100-000-$id",
            balance = Money.from(balance)
        )

    private class InMemoryAccountRepoFake(
        accounts: List<Account>
    ) : AccountRepository {

        private val data = accounts.associateBy { it.id }.toMutableMap()

        override suspend fun getById(id: AccountId): Account? = data[id]

        override suspend fun update(account: Account) {
            data[account.id] = account
        }
    }

    private class InMemoryTransactionRepoFake : TransactionRepository {

        private val data = mutableListOf<Transaction>()

        override suspend fun insert(transaction: Transaction) {
            data += transaction
        }

        override suspend fun getAll(): List<Transaction> = data.toList()
    }
}
