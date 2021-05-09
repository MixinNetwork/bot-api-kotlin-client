package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class TransactionResponse(
    val type: String,
    @SerializedName("snapshot_id")
    val snapshotId: String,
    @SerializedName("opponent_receivers")
    val opponentReceivers: List<String>,
    @SerializedName("opponent_threshold")
    val opponentThreshold: Int,
    @SerializedName("asset_id")
    val assetId: String,
    val amount: String,
    @SerializedName("trace_id")
    val traceId: String,
    val memo: String?,
    @SerializedName("created_at")
    val createdAt: String
)