package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class Ticker(
    @SerializedName("price_usd")
    val priceUsd: String,
    @SerializedName("price_btc")
    val priceBtc: String,
)
