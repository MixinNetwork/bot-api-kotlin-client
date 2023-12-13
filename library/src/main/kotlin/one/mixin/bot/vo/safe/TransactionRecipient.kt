package one.mixin.bot.vo.safe

data class TransactionRecipient(
    val mixAddress: MixAddress,
    val amount: String,
    val destination: String? = null,
    val tag: String? = null,
)
