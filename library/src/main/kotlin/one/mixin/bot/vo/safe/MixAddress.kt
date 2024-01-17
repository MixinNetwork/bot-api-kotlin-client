package one.mixin.bot.vo.safe

import one.mixin.bot.extension.isUUID
import one.mixin.bot.util.UUIDUtils
import one.mixin.bot.util.decodeBase58
import one.mixin.bot.util.encodeToBase58String
import one.mixin.bot.util.sha3Sum256

const val MixAddressPrefix = "MIX"
const val MainAddressPrefix = "XIN"
private const val MixAddressVersion = 0x2.toByte()

data class MixAddress(
    val version: Byte,
    val threshold: Byte,
) {
    val uuidMembers = mutableListOf<String>()
    val xinMembers = mutableListOf<String>()

    companion object {
        fun newUuidMixAddress(
            members: List<String>,
            threshold: Int,
        ): MixAddress? {
            return MixAddress(MixAddressVersion, threshold.toByte()).apply {
                members.map {
                    if (!it.isUUID()) return null
                }
                uuidMembers.addAll(members)
            }
        }

        fun newMainnetMixAddress(
            members: List<String>,
            threshold: Int,
        ): MixAddress {
            return MixAddress(MixAddressVersion, threshold.toByte()).apply {
                xinMembers.addAll(members)
            }
        }
    }

    fun members(): List<String> {
        return if (uuidMembers.size > 0) {
            uuidMembers
        } else {
            xinMembers
        }
    }

    override fun toString(): String {
        var payload = byteArrayOf(version, threshold)
        var len = uuidMembers.size
        if (len > 0) {
            if (len > 255) {
                return ""
            }
            payload += len.toByte()
            for (u in uuidMembers) {
                payload += UUIDUtils.toByteArray(u)
            }
        } else {
            len = xinMembers.size
            if (len > 255) {
                return ""
            }
            payload += len.toByte()
            for (x in xinMembers) {
                payload += x.mainnetAddressToPublic()
            }
        }
        val data = MixAddressPrefix.toByteArray() + payload
        val checksum = data.sha3Sum256()
        payload += checksum.sliceArray(0..3)
        return MixAddressPrefix + payload.encodeToBase58String()
    }
}


// [return] ByteArray, size 64. [0,32) is publicSpendKey, [32,64) is publicViewKey
fun String.mainnetAddressToPublic(): ByteArray {
    if (!startsWith(MainAddressPrefix)) {
        throw Exception("invalid address network")
    }
    val data = substring(MainAddressPrefix.length).decodeBase58()
    if (data.size != 68) {
        throw Exception("invalid address format")
    }
    val payload = data.sliceArray(0 until 64)
    val checksum = (MainAddressPrefix.toByteArray() + payload).sha3Sum256()
    if (!checksum.sliceArray(0..3).contentEquals(data.sliceArray(64 until 68))) {
        throw Exception("invalid address checksum")
    }
    return payload
}

fun ByteArray.publicToMainnetAddress(): String {
    val checksum = (MainAddressPrefix.toByteArray() + this).sha3Sum256()

    val data = this + checksum.sliceArray(0..3)
    return MainAddressPrefix + data.encodeToBase58String()
}

fun String.toMixAddress(): MixAddress? {
    if (!this.startsWith(MixAddressPrefix)) return null

    val data = try {
        this.removePrefix(MixAddressPrefix).decodeBase58()
    } catch (e: Exception) {
        println("decodeBase58 with $this meet $e")
        return null
    }
    if (data.size < 3 + 16 + 4) return null

    val payload = data.sliceArray(0..data.size - 5)
    val checksum = (MixAddressPrefix.toByteArray() + payload).sha3Sum256().sliceArray(0..3)
    if (!checksum.contentEquals(data.sliceArray(data.size - 4..<data.size))) {
        return null
    }

    val version = payload[0]
    if (version != MixAddressVersion) return null
    val threshold = payload[1]
    val total = payload[2].toInt()
    if (threshold.toInt() == 0 || threshold > total || total > 64) return null
    val mixAddress = MixAddress(version, threshold)
    val mb = payload.sliceArray(3..<payload.size)
    when (mb.size) {
        16 * total -> {
            for (i in 0..<total) {
                val id = UUIDUtils.fromByteArray(mb.sliceArray(i * 16..<i * 16 + 16))
                mixAddress.uuidMembers.add(id)
            }
        }

        64 * total -> {
            for (i in 0..<total) {
                val pub = mb.sliceArray(i * 64..<i * 64 + 64)
                mixAddress.xinMembers.add(pub.publicToMainnetAddress())
            }
        }

        else -> {
            return null
        }
    }
    return mixAddress
}
