package com.aleksandar.paymenttransferservice_android.data.repository

import com.aleksandar.paymenttransferservice_android.core.AccountId
import com.aleksandar.paymenttransferservice_android.core.Money
import com.aleksandar.paymenttransferservice_android.domain.model.Account
import com.aleksandar.paymenttransferservice_android.domain.repository.AccountRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryAccountRepository : AccountRepository {

    // Internal data store, just for this test task
    private val accounts = mutableMapOf<AccountId, Account>()

    private val mutex = Mutex()

    override suspend fun getById(id: AccountId): Account? = mutex.withLock {
        accounts[id]
    }

    override suspend fun update(account: Account) = mutex.withLock {
        accounts[account.id] = account
    }

    /**
     * Helper for initializing accounts during demo/testing.
     */
    suspend fun addInitialAccounts() = mutex.withLock {
        if (accounts.isNotEmpty()) return@withLock

        val acc1 = Account(
            id = AccountId("ACC-001"),
            fullName = "Aleksandar Adamovic",
            accountNumber = "100-000-0001",
            balance = Money.from(500.00)
        )
        val acc2 = Account(
            id = AccountId("ACC-002"),
            fullName = "Jane Smith",
            accountNumber = "100-000-0002",
            balance = Money.from(200.00)
        )

        val acc3 = Account(
            id = AccountId("ACC-003"),
            fullName = "Joe Carlson",
            accountNumber = "100-000-0003",
            balance = Money.from(100.00)
        )

        val acc4 = Account(
            id = AccountId("ACC-004"),
            fullName = "LeBron James",
            accountNumber = "100-000-0004",
            balance = Money.from(50.00)
        )

        accounts[acc1.id] = acc1
        accounts[acc2.id] = acc2
        accounts[acc3.id] = acc3
        accounts[acc4.id] = acc4
    }

    /**
     * For debugging and testing.
     */
    suspend fun getAllAccounts(): List<Account> = mutex.withLock {
        accounts.values.toList()
    }
}
