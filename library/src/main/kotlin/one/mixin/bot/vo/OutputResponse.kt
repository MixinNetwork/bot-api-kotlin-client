package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

class OutputResponse(
    val type: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("utxo_id")
    val utxoId: String,
    @SerializedName("asset_id")
    val assetId: String,
    @SerializedName("transaction_hash")
    val transactionHash: String,
    @SerializedName("output_index")
    val outputIndex: Int,
    val amount: String,
    val threshold: String,
    val members: List<String>,
    val memo: String,
    val state: String,
    val sender: String,
    @SerializedName("signed_tx")
    val signedTx: String,
    @SerializedName("signed_by")
    val signedBy: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
)
