package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("pin_token")
    val pinToken: String,
    @SerializedName("identity_number")
    val identityNumber: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("avatar_url")
    val avatarURL: String,
    @SerializedName("created_at")
    val createdAt: String
)
