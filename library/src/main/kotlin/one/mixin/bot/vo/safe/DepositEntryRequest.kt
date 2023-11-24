package one.mixin.bot.vo.safe

import com.google.gson.annotations.SerializedName

data class DepositEntryRequest(
    @SerializedName("chain_id")
    val chainId: String,
)