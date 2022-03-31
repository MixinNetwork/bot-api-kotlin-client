package one.mixin.bot.util

@ExperimentalUnsignedTypes
fun toLeByteArray(v: UInt): ByteArray {
    val b = ByteArray(2)
    b[0] = v.toByte()
    b[1] = (v shr 8).toByte()
    return b
}

@ExperimentalUnsignedTypes
fun leByteArrayToInt(bytes: ByteArray): UInt {
    return bytes[0].toUInt() + (bytes[1].toUInt() shl 8)
}
