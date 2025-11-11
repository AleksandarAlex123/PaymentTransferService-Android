package com.aleksandar.paymenttransferservice_android.core

@JvmInline
value class AccountId(val raw: String) {
    init {
        // This is just debug error, not UI. No need to add in res
        require(raw.isNotBlank()) { "AccountId cannot be blank" }
    }

    override fun toString(): String = raw
}
