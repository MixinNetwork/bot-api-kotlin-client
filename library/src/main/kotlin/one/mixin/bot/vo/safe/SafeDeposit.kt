package one.mixin.bot.vo.safe

import com.google.gson.annotations.SerializedName

class SafeDeposit(
    @SerializedName("deposit_hash")
    val depositHash: String,
    @SerializedName("sender")
    val sender: String = "",
)
