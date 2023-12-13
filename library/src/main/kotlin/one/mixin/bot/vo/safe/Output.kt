package one.mixin.bot.vo.safe

import com.google.gson.annotations.SerializedName

data class Output(
    @SerializedName("output_id")
    val outputId: String,
    @SerializedName("transaction_hash")
    val transactionHash: String,
    @SerializedName("output_index")
    val outputIndex: Int,
    @SerializedName("asset")
    val asset: String,
    @SerializedName("sequence")
    val sequence: Long,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("mask")
    val mask: String,
    @SerializedName("keys")
    val keys: List<String>,
    @SerializedName("receivers")
    val receivers: List<String>,
    @SerializedName("receivers_hash")
    val receiversHash: String,
    @SerializedName("receivers_threshold")
    val receiversThreshold: Int,
    val extra: String,
    val state: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("signed_by")
    val signedBy: String,
    @SerializedName("signed_at")
    val signedAt: String,
    @SerializedName("spent_at")
    val spentAt: String,
)
