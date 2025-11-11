package com.aleksandar.paymenttransferservice_android.core.util

object ValidationHelper {
    private val accountIdRegex = Regex("^[A-Za-z0-9-]{3,20}$")
    private val amountRegex = Regex("^[0-9]+(\\.[0-9]{1,2})?$")

    fun isValidAccountId(input: String?): Boolean {
        if (input.isNullOrBlank()) return false
        return accountIdRegex.matches(input)
    }

    fun isValidAmount(input: String?): Boolean {
        if (input.isNullOrBlank()) return false
        return amountRegex.matches(input)
    }
}
