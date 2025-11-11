package com.aleksandar.paymenttransferservice_android.domain.repository

import com.aleksandar.paymenttransferservice_android.core.AccountId
import com.aleksandar.paymenttransferservice_android.domain.model.Account

interface AccountRepository {
    suspend fun getById(id: AccountId): Account?
    suspend fun update(account: Account)
}
