package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class PinRequest(
    val pin: String,
    @SerializedName("old_pin")
    val oldPin: String? = null,
    @SerializedName("salt_base64")
    var salt: String? = null,
    @SerializedName("old_salt_base64")
    var oldSalt: String? = null,
)
