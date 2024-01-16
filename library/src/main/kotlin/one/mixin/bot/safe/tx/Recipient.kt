package one.mixin.bot.safe.tx

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger

sealed interface TransactionRecipient {

    val amount: String

    fun amountInEthUnit(decimals: Int = 8): BigInteger {
        val a = BigDecimal.parseString(amount)
        return (a * BigDecimal.TEN.pow(decimals)).toBigInteger()
    }

    data class Withdrawal(
        val destination: String,
        val tag: String?,
        override val amount: String,
    ) : TransactionRecipient

    data class User(
        val members: List<String>,
        override val amount: String,
        val threshold: Int,
    ) : TransactionRecipient {
        val script: String
            get() = encodeScript(threshold)
    }
}

private fun encodeScript(threshold: Int): String {
    var s = threshold.toString(16)
    if (s.length == 1) {
        s = "0$s"
    }
    if (s.length > 2) {
        throw Exception("invalid threshold. $threshold")
    }
    return "fffe$s"
}