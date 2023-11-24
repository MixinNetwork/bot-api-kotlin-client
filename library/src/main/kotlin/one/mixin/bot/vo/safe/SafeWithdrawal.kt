package one.mixin.bot.vo.safe

import com.google.gson.annotations.SerializedName

class SafeWithdrawal(
    @SerializedName("withdrawal_hash")
    val withdrawalHash: String,
    @SerializedName("receiver")
    val receiver: String,
)