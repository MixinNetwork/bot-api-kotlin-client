package one.mixin.bot.extension

import java.util.Base64

fun String.base64Encode() = toByteArray().base64Encode()

fun ByteArray.base64Encode(): String = Base64.getEncoder().encodeToString(this)

fun String.base64Decode(): ByteArray = Base64.getDecoder().decode(this)

fun ByteArray.base64UrlEncode(): String = Base64.getUrlEncoder().withoutPadding().encodeToString(this)

fun String.base64UrlDecode(): ByteArray = Base64.getUrlDecoder().decode(this)
