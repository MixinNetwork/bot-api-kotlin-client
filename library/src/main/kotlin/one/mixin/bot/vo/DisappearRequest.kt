package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class DisappearRequest(
    @SerializedName("duration")
    val duration: Long,
)
