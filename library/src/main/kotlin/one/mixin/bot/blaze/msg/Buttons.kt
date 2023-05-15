package one.mixin.bot.blaze.msg

import com.google.gson.annotations.SerializedName

data class Buttons(
    @SerializedName("label") var label: String,
    @SerializedName("color") var color: String,
    @SerializedName("action") var action: String,
) {
    override fun toString(): String {
        return "Buttons(label='$label', color='$color', action='$action')"
    }
}
