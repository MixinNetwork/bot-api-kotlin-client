package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("address_id")
    val addressId: String,
    val type: String,
    @SerializedName("asset_id")
    val assetId: String,
    @SerializedName("destination")
    val destination: String,
    val label: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val reserve: String,
    val fee: String,
    @SerializedName("tag")
    val tag: String?,
    @SerializedName("dust")
    val dust: String?
)

fun Address.displayAddress(): String {
    return if (!tag.isNullOrEmpty()) {
        "$destination:$tag"
    } else {
        destination
    }
}
