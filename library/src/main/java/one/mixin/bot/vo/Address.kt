
package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class Address(
    val type: String,
    @SerializedName("address_id")
    val addressId: String,
    @SerializedName("asset_id")
    val assetId: String,
    @SerializedName("public_key")
    val publicKey: String?,
    val label: String?,
    @SerializedName("updated_at")
    val updatedAt: String,
    val reserve: String,
    val fee: String,
    @SerializedName("account_name")
    val accountName: String?,
    @SerializedName("account_tag")
    val accountTag: String?,
    @SerializedName("dust")
    val dust: String?
)
