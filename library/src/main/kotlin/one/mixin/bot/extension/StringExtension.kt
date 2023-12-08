package one.mixin.bot.extension

import java.math.BigDecimal

fun String.stripAmountZero(): String {
    return BigDecimal(this).stripTrailingZeros().toPlainString()
}