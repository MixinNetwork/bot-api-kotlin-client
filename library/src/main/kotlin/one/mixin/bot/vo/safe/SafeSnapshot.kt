package one.mixin.bot.vo.safe

import com.google.gson.annotations.SerializedName

data class SafeSnapshot(
    @SerializedName("snapshot_id")
    val snapshotId: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("asset_id")
    val assetId: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("opponent_id")
    val opponentId: String,
    @SerializedName("memo")
    val memo: String,
    @SerializedName("transaction_hash")
    val transactionHash: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("trace_id")
    val traceId: String?,
    @SerializedName("confirmations")
    val confirmations: Int?,
    @SerializedName("opening_balance")
    val openingBalance: String?,
    @SerializedName("closing_balance")
    val closingBalance: String?,
    @SerializedName("deposit")
    val deposit: SafeDeposit?,
    @SerializedName("withdrawal")
    val withdrawal: SafeWithdrawal?,
) {
    @SerializedName("deposit_hash")
    val depositHash: String? = null
}
