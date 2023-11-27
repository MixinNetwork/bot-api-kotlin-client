package one.mixin.bot.vo.tip

import com.google.gson.annotations.SerializedName

data class TipGas(
    @SerializedName("asset_id")
    val assetId: String,
    @SerializedName("safe_gas_price")
    val safeGasPrice: String,
    @SerializedName("propose_gas_price")
    val proposeGasPrice: String,
    @SerializedName("fast_gas_price")
    val fastGasPrice: String,
    @SerializedName("gas_limit")
    val gasLimit: String,
)