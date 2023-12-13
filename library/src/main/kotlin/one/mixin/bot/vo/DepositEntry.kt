package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

class DepositEntry(
    @SerializedName("destination")
    val destination: String,
    @SerializedName("tag")
    val tag: String?,
    @SerializedName("properties")
    val properties: List<String>?,
)
