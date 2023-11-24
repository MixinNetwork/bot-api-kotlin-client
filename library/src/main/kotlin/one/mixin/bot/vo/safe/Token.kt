package one.mixin.bot.vo.safe

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("asset_id")
    val assetId: String,
    @SerializedName("kernel_asset_id")
    val asset: String,
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("icon_url")
    val iconUrl: String,
    @SerializedName("price_btc")
    val priceBtc: String,
    @SerializedName("price_usd")
    val priceUsd: String,
    @SerializedName("chain_id")
    val chainId: String,
    @SerializedName("change_usd")
    val changeUsd: String,
    @SerializedName("change_btc")
    val changeBtc: String,
    @SerializedName("confirmations")
    val confirmations: Int,
    @SerializedName("asset_key")
    val assetKey: String,
    @SerializedName("dust")
    val dust: String,
)