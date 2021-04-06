package one.mixin.bot.util

import java.util.Base64

fun base64Encode(src: ByteArray): String? {
    return Base64.getEncoder().encodeToString(src)
}

fun base64Decode(src: String): ByteArray? {
    return Base64.getDecoder().decode(src)
}
