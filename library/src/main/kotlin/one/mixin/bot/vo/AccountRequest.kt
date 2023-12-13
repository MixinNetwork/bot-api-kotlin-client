package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class AccountRequest(
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("session_secret")
    val sessionSecret: String,
)
