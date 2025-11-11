package com.aleksandar.paymenttransferservice_android.core

import java.math.BigDecimal

@JvmInline
value class Money(val value: BigDecimal) : Comparable<Money> {

    init {
        // This is just debug error, not UI. No need to add in res
        require(value.scale() <= 2) { "Money supports up to 2 decimal places" }
    }

    operator fun plus(other: Money): Money = Money(value + other.value)

    operator fun minus(other: Money): Money = Money(value - other.value)

    override fun compareTo(other: Money): Int = value.compareTo(other.value)

    fun isPositive(): Boolean = value > BigDecimal.ZERO

    fun isNonPositive(): Boolean = value <= BigDecimal.ZERO

    override fun toString(): String = value.toPlainString()

    companion object {
        fun zero(): Money = Money(BigDecimal.ZERO.setScale(2))

        fun from(amount: String): Money =
            Money(amount.toBigDecimal().setScale(2))

        fun from(amount: Long): Money =
            Money(BigDecimal(amount).setScale(2))

        fun from(amount: Double): Money =
            Money(amount.toBigDecimal().setScale(2))
    }
}
