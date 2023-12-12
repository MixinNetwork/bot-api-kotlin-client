package one.mixin.bot.vo

data class Utxo(
    val hash: String,
    val amount: String,
    val index: Int = 1,
)