package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class Asset(
    @SerializedName("asset_id")
    val assetId: String,
    val symbol: String,
    val name: String,
    @SerializedName("icon_url")
    val iconUrl: String,
    val balance: String,
    val destination: String,
    @SerializedName("deposit_entries")
    val depositEntries: List<DepositEntry>?,
    val tag: String?,
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
    val confirmations: Int,
    @SerializedName("asset_key")
    val assetKey: String?,
    val reserve: String?
)
