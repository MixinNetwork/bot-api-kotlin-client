package one.mixin.bot.vo.tip

import com.google.gson.annotations.SerializedName

data class TipSecretRequest(
    @SerializedName("action")
    val action: String,
    @SerializedName("seed_base64")
    val seedBase64: String? = null,
    @SerializedName("secret_base64")
    val secretBase64: String? = null,
    @SerializedName("signature_base64")
    val signatureBase64: String,
    @SerializedName("timestamp")
    val timestamp: Long,
)