package one.mixin.bot.vo.tip

import com.google.gson.annotations.SerializedName

data class TipRequest(
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("seed_base64")
    val seedBase64: String?,
)
