package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

class MultisigsResponse(
    val type: String,
    @SerializedName("code_id")
    val codeId: String,
    @SerializedName("request_id")
    val requestId: String,
    val action: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("asset_id")
    val assetId: String,
    val amount: String,
    val senders: Array<String>,
    val receivers: Array<String>,
    val threshold: Int,
    val state: String,
    @SerializedName("transaction_hash")
    val transactionHash: String,
    @SerializedName("raw_transaction")
    val rawTransaction: String,
    @SerializedName("created_at")
    val createdAt: String,
    val memo: String?,
)
