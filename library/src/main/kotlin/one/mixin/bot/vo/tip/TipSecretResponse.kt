package one.mixin.bot.vo.tip

import com.google.gson.annotations.SerializedName

class TipSecretResponse(
    @SerializedName("seed_base64")
    val seedBase64: String? = null,
)
