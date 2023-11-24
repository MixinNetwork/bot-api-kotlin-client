package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

enum class WithdrawalMemoPossibility(val value: String) {
    @SerializedName("negative")
    NEGATIVE("negative"),

    @SerializedName("possible")
    POSSIBLE("possible"),

    @SerializedName("positive")
    POSITIVE("positive"),
}
