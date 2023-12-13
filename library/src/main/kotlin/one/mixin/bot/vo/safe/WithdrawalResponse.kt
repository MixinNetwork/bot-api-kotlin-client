package one.mixin.bot.vo.safe

import com.google.gson.annotations.SerializedName

data class WithdrawalResponse(
    @SerializedName("asset_id")
    val assetId: String?,
    val amount: String?,
)
