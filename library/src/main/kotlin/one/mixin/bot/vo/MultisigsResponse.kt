package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

class MultisigsResponse(
    val type: String,
    @SerializedName("request_id")
    val requestId: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("asset_id")
    val assetId: String,
    val amount: String,
    val threshold: String,
    val senders: List<String>,
    val receivers: List<String>,
    val signers: List<String>,
    val memo: String?,
    val action: String,
    val state: String,
    @SerializedName("transaction_hash")
    val transactionHash: String,
    @SerializedName("raw_transaction")
    val rawTransaction: String,
    @SerializedName("created_at")
    val created_at: String,
    @SerializedName("code_id")
    val codeId: String
)
