package one.mixin.library.vo

import com.google.gson.annotations.SerializedName

data class AddressesRequest(
    @SerializedName("asset_id")
    val assetId: String,
    @SerializedName("public_key")
    val publicKey: String,
    val label: String,
    val pin: String,
    @SerializedName("account_name")
    val accountName: String? = null,
    @SerializedName("account_tag")
    val accountTag: String? = null
)