package one.mixin.bot.extension

import one.mixin.bot.util.sha3Sum256
import java.math.BigDecimal
import java.util.UUID

fun String.stripAmountZero(): String {
    return BigDecimal(this).stripTrailingZeros().toPlainString()
}

fun String.isUUID(): Boolean {
    return try {
        return UUID.fromString(this) != null
    } catch (exception: IllegalArgumentException) {
        false
    }
}

fun assetIdToAsset(assetId: String): String {
    assert(assetId.isUUID())
    return assetId.sha3Sum256()
        .joinToString("") { "%02x".format(it) }
}
