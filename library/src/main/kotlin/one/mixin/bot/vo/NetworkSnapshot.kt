package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class NetworkSnapshot(
    val amount: String,
    @SerializedName("asset")
    val asset: Asset,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("snapshot_id")
    val snapshotId: String,
    @SerializedName("source")
    val source: String,
    val type: String?,
    @SerializedName("user_id")
    val userId: String?,
    @SerializedName("trace_id")
    val traceId: String?,
    @SerializedName("opponent_id")
    val opponentId: String?,
    @SerializedName("data")
    val memo: String?,
)
