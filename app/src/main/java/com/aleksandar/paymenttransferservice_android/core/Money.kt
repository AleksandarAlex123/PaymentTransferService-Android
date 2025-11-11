package com.aleksandar.paymenttransferservice_android.core

import java.math.BigDecimal
import java.math.RoundingMode

@JvmInline
value class Money(val value: BigDecimal) : Comparable<Money> {

    init {
        // This is a debug validation (not a localized user message), no need to put in res
        require(value.scale() <= 2) { "Money supports up to 2 decimal places" }
    }

    // Adds two monetary values (same currency)
    operator fun plus(other: Money): Money = Money(value + other.value)

    // Subtracts another monetary value (same currency).
    operator fun minus(other: Money): Money = Money(value - other.value)

    // Compares two amounts
    override fun compareTo(other: Money): Int = value.compareTo(other.value)

    // Returns true if the amount is positive
    fun isPositive(): Boolean = value > BigDecimal.ZERO

    // Returns true if the amount is zero or negative
    fun isNonPositive(): Boolean = value <= BigDecimal.ZERO

    override fun toString(): String =
        "${CURRENCY_SYMBOL}${value.setScale(2, RoundingMode.HALF_UP).toPlainString()}"

    companion object {
        const val DEFAULT_CURRENCY = "EUR"

        private const val CURRENCY_SYMBOL = "€"

        fun zero(): Money = Money(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))

        fun from(amount: String): Money =
            Money(amount.toBigDecimal().setScale(2, RoundingMode.HALF_UP))

        fun from(amount: Long): Money =
            Money(BigDecimal(amount).setScale(2, RoundingMode.HALF_UP))

        fun from(amount: Double): Money =
            Money(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP))
    }
}
