package one.mixin.bot.util

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import okio.Buffer
import okio.ByteString.Companion.toByteString
import one.mixin.eddsa.Ed25519
import one.mixin.eddsa.Field25519
import kotlin.experimental.and
import kotlin.experimental.or


private fun ByteArray.toNumberLE(): BigInteger {
    return BigInteger.fromByteArray(this.reversedArray(), Sign.POSITIVE)
}

internal fun BigInteger.toBytesLE(size: Int = 32): ByteArray {
    val bytes = this.toByteArray().reversedArray()
    return if (bytes.size == size) {
        bytes
    } else {
        bytes.copyOf(size)
    }

}

internal object Ed25519Util {

    // 2^252 + 27742317777372353535851937790883648493
    private val L =
        BigInteger.parseString("7237005577332262213973186563042994240857116359379907606001950938285454250989")

    // The order of the generator as unsigned bytes in little endian order.
    // (2^252 + 0x14def9dea2f79cd65812631a5cf5d3ed, cf. RFC 7748)
    private val GROUP_ORDER = byteArrayOf(
        0xed.toByte(),
        0xd3.toByte(),
        0xf5.toByte(),
        0x5c.toByte(),
        0x1a.toByte(),
        0x63.toByte(),
        0x12.toByte(),
        0x58.toByte(),
        0xd6.toByte(),
        0x9c.toByte(),
        0xf7.toByte(),
        0xa2.toByte(),
        0xde.toByte(),
        0xf9.toByte(),
        0xde.toByte(),
        0x14.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x10.toByte()
    )

    fun setBytesWithClamping(bytes: ByteArray): BigInteger {
        require(bytes.size == 32) { "invalid SetBytesWithClamping input length: ${bytes.size}" }

        val wideBytes = bytes.copyOf(64)

        wideBytes[0] = wideBytes[0] and 248.toByte()
        wideBytes[31] = wideBytes[31] and 127.toByte()
        wideBytes[31] = wideBytes[31] or 64.toByte()

        val numberLE = wideBytes.sliceArray(0 until 32).toNumberLE()
        return numberLE % L
    }

    fun setUniformBytes(bytes: ByteArray): BigInteger {
        require(bytes.size == 64) { "invalid SetUniformBytes input length: ${bytes.size}" }
        return bytes.toNumberLE() % L
    }

    private fun isReduced(s: ByteArray): Boolean {
        for (j in Field25519.FIELD_LEN - 1 downTo 0) { // compare unsigned bytes
            val a = s[j].toInt() and 0xff
            val b = GROUP_ORDER[j].toInt() and 0xff
            if (a != b) {
                return a < b
            }
        }
        return false
    }

    fun setCanonicalBytes(bytes: ByteArray): BigInteger {
        require(bytes.size == 32) { "invalid SetCanonicalBytes input length: ${bytes.size}" }
        if (!isReduced(bytes)) {
            throw IllegalArgumentException("invalid SetCanonicalBytes input")
        }
        return bytes.toNumberLE()
    }

    fun scalarAdd(a: BigInteger, b: BigInteger): BigInteger {
        return (a + b) % L
    }

    fun publicKey(privateKey: ByteArray): ByteArray {
        val x = setCanonicalBytes(privateKey)
        val y = Ed25519.scalarMultWithBase(x.toBytesLE(), true)
        return y.toBytes()
    }


    fun sign(message: ByteArray, privateKey: ByteArray): ByteArray {
        val digest1 = privateKey.sha512()
        val messageDigest = Buffer().apply {
            write(digest1.sliceArray(32 until 64))
            write(message)
        }.sha512().toByteArray()

        val z = setUniformBytes(messageDigest)
        val r = Ed25519.scalarMultWithBase(z.toBytesLE(64), true).toBytes().copyOfRange(0, Field25519.FIELD_LEN)

        val pub = publicKey(privateKey)

        val hramDigest = Buffer().apply {
            write(r)
            write(pub)
            write(message)
        }.sha512().toByteArray()

        val x = setUniformBytes(hramDigest)
        val y = setCanonicalBytes(privateKey)

        val s = (x * y + z) % L

        return Buffer().apply {
            write(r)
            write(s.toBytesLE())
        }.snapshot().toByteArray()

    }


}