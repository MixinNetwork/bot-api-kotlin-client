package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class TransactionResponse(
    val type: String,
    @SerializedName("snapshot_id")
    val snapshotId: String,
    @SerializedName("opponent_key")
    val opponentKey:String?,
    @SerializedName("opponent_receivers")
    val opponentReceivers: List<String>,
    @SerializedName("opponent_threshold")
    val opponentThreshold: Int,
    @SerializedName("asset_id")
    val assetId: String,
    val amount: String,
    @SerializedName("opening_balance")
    val openingBalance:String?,
    @SerializedName("closing_balance")
    val closingBalance:String?,
    @SerializedName("trace_id")
    val traceId: String,
    val memo: String?,
    val state:String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("transaction_hash")
    val transactionHash: String?,
    @SerializedName("snapshot_hash")
    val snapshotHash: String?,
    @SerializedName("snapshot_at")
    val snapshotAt: String?,
)
