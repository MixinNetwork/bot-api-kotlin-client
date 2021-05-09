package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class TransactionRequest(
    @SerializedName("asset_id")
    val assetId: String,
    @SerializedName("opponent_multisig")
    val opponentMultisig: OpponentMultisig,
    val amount: String,
    val pin: String,
    @SerializedName("trace_id")
    val traceId: String?,
    val memo: String?
)

data class OpponentMultisig(
    val receivers: List<String>,
    val threshold: Int
)