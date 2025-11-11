package com.aleksandar.paymenttransferservice_android.di

import com.aleksandar.paymenttransferservice_android.data.repository.InMemoryAccountRepository
import com.aleksandar.paymenttransferservice_android.data.repository.InMemoryTransactionRepository
import com.aleksandar.paymenttransferservice_android.domain.repository.AccountRepository
import com.aleksandar.paymenttransferservice_android.domain.repository.TransactionRepository
import com.aleksandar.paymenttransferservice_android.domain.transfer.TransferFundsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAccountRepository(): AccountRepository {
        // Placed in-memory accounts for demo purposes.
        return InMemoryAccountRepository().also { repo ->
            runBlocking {
                repo.addInitialAccounts()
            }
        }
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(): TransactionRepository {
        return InMemoryTransactionRepository()
    }

    @Provides
    @Singleton
    fun provideTransferFundsUseCase(
        accountRepository: AccountRepository,
        transactionRepository: TransactionRepository
    ): TransferFundsUseCase {
        return TransferFundsUseCase(
            accountRepository = accountRepository,
            transactionRepository = transactionRepository
        )
    }
}
