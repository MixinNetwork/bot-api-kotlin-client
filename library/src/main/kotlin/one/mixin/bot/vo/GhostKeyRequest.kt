package one.mixin.bot.vo

data class GhostKeyRequest(
    val receivers: List<String>,
    val index: Int,
    val hint: String,
)
