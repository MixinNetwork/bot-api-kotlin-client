package one.mixin.bot.util

import okio.Buffer

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

fun Long.toBeByteArray(): ByteArray {
    val buffer = Buffer()
    buffer.writeLong(this)
    return buffer.readByteArray()
}
