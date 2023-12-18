package one.mixin.bot.extension

private val HEX_CHARS = "0123456789abcdef"

fun ByteArray.toHex(): String {
    val hex = HEX_CHARS.toCharArray()
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(hex[firstIndex])
        result.append(hex[secondIndex])
    }

    return result.toString()
}

fun String.toHex() = toByteArray().toHex()

fun String.hexStringToByteArray(): ByteArray {
    val result = ByteArray(length / 2)
    for (i in 0 until length step 2) {
        val firstIndex = HEX_CHARS.indexOf(this[i])
        val secondIndex = HEX_CHARS.indexOf(this[i + 1])

        val octet = firstIndex.shl(4).or(secondIndex)
        result[i.shr(1)] = octet.toByte()
    }
    return result
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
