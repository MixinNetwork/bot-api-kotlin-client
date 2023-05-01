package one.mixin.bot.blaze.msg

import com.google.gson.annotations.SerializedName

data class Cards(
    @SerializedName("app_id") var appId: String,
    @SerializedName("icon_url") var iconUrl: String,
    @SerializedName("title") var title: String,
    @SerializedName("description") var description: String,
    @SerializedName("action") var action: String,
    @SerializedName("shareable") var shareable: Boolean? = true,
) {
    override fun toString(): String {
        return "Cards(appId='$appId', iconUrl='$iconUrl', title='$title', description='$description', action='$action', shareable=$shareable)"
    }
}