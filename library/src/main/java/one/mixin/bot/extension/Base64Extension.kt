package one.mixin.bot.extension

import java.lang.Exception
import java.util.Base64

fun String.base64Encode() = toByteArray().base64Encode()

fun ByteArray.base64Encode(): String = Base64.getEncoder().encodeToString(this)

fun String.base64Decode(): ByteArray {
    return try {
        Base64.getUrlDecoder().decode(this)
    } catch (e: Exception) {
        Base64.getDecoder().decode(this)
    }
}

fun Long.toLeByteArray(): ByteArray {
    var num = this
    val result = ByteArray(8)
    for (i in (0..7)) {
        result[i] = (num and 0xffL).toByte()
        num = num shr 8
    }
    return result
}