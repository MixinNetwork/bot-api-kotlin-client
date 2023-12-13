package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

class TransferRequest(
    @SerializedName("asset_id")
    val assetId: String,
    @SerializedName("opponent_id")
    val opponentId: String?,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("pin")
    val pin: String?,
    @SerializedName("trace_id")
    val traceId: String? = null,
    @SerializedName("memo")
    val memo: String? = null,
    // only for validate payment
    @SerializedName("address_id")
    val addressId: String? = null,
)
