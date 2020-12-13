package one.mixin.bot.vo

data class PaymentResponse(
    val status: String
)

enum class PaymentStatus {
    pending, paid
}
