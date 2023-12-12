package one.mixin.bot.safe

import java.math.BigDecimal

data class UtxoException(
    val totalInput: BigDecimal,
    val totalOutput: BigDecimal,
    val outputSize: Int,
) : RuntimeException()