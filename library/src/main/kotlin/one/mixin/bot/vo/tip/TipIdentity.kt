package one.mixin.bot.vo.tip

import com.google.gson.annotations.SerializedName

class TipIdentity(
    @SerializedName("seed_base64")
    val seedBase64: String,
)
