package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class Billing (
    val type: String,
    @SerializedName("app_id")
    val appId: String,
    val cost: Cost,
    val credit:String,
)

data class Cost (
    val users: String,
    val resources: String
)
