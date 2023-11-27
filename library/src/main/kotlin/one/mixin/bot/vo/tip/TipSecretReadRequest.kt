package one.mixin.bot.vo.tip

import com.google.gson.annotations.SerializedName

data class TipSecretReadRequest(
    @SerializedName("signature_base64")
    val signatureBase64: String,
    @SerializedName("timestamp")
    val timestamp: Long,
) {
    @SerializedName("action")
    val action: String = TipSecretAction.READ.name
}

enum class TipSecretAction {
    READ,
    UPDATE,
}