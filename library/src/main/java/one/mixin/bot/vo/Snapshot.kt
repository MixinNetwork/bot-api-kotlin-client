package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class Snapshot(
    val type: String,
    @SerializedName("snapshot_id")
    val snapshotId: String,
    @SerializedName("transaction_hash")
    val transactionHash: String,
    @SerializedName("asset_id")
    val assetId: String,
    val amount: String,
    @SerializedName("trace_id")
    val traceId: String,
    val memo: String,
    @SerializedName("created_at")
    val createdAt: String
)
