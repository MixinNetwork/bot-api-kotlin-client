package one.mixin.bot.vo.safe

import kernel.Utxo

data class SignResult(
    val raw: String,
    val change: Utxo?,
)
