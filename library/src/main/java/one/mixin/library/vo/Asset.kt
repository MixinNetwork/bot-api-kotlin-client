package one.mixin.library.vo

import com.google.gson.annotations.SerializedName

data class Asset(
    @SerializedName("asset_id")
    val assetId: String,
    @SerializedName("chain_id")
    val chainId: String,
    val symbol: String,
    val name: String,
    @SerializedName("icon_url")
    val iconUrl: String,
    val balance: String,
    @SerializedName("public_key")
    val publicKey: String?,
    @SerializedName("price_btc")
    val priceBtc: String,
    @SerializedName("price_usd")
    val priceUsd: String
)
