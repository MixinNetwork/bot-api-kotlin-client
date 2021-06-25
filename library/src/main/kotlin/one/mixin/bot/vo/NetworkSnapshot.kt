package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class NetworkSnapshot(
    @SerializedName("snapshot_id")
    val snapshotId: String,
    val type: String,
    val amount: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("asset")
    val asset: Asset,
    @SerializedName("source")
    val source: String
)