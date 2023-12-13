package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class Snapshot(
    @SerializedName("snapshot_id")
    val snapshotId: String,
    val type: String,
    @SerializedName("asset_id")
    val assetId: String,
    val amount: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("opponent_id")
    val opponentId: String?,
    @SerializedName("trace_id")
    val traceId: String?,
    @SerializedName("transaction_hash")
    val transactionHash: String?,
    val sender: String?,
    val receiver: String?,
    val memo: String?,
    val confirmations: Int?,
)

@Suppress("EnumEntryName")
enum class SnapshotType { transfer, deposit, withdrawal, fee, rebate, raw, pending }
