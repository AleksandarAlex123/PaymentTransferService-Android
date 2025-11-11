package com.aleksandar.paymenttransferservice_android.core

@JvmInline
value class AccountId(val raw: String) {
    init {
        require(raw.isNotBlank()) { "AccountId cannot be blank" }
    }

    override fun toString(): String = raw
}
