package one.mixin.bot.vo

data class Quota(
    val type: String,
    val name: String,
    val used: Int,
    val remaining: Int,
)
