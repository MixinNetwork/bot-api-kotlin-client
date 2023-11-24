package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class Chain(
    @SerializedName("chain_id")
    val chainId: String,
    val name: String,
    val symbol: String,
    @SerializedName("icon_url")
    val iconUrl: String,
    val threshold: Int,
    @SerializedName("withdrawal_memo_possibility")
    val withdrawalMemoPossibility: String = WithdrawalMemoPossibility.POSSIBLE.name,
)
