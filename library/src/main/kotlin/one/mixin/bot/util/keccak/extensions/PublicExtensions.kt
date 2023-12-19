package one.mixin.bot.util.keccak.extensions

import one.mixin.bot.util.keccak.Keccak
import one.mixin.bot.util.keccak.KeccakParameter

/**
 * Computes the proper Keccak digest of [this] byte array based on the given [parameter]
 */
public fun ByteArray.digestKeccak(parameter: KeccakParameter): ByteArray {
    return Keccak.digest(this, parameter)
}

/**
 * Computes the proper Keccak digest of [this] string based on the given [parameter]
 */
public fun String.digestKeccak(parameter: KeccakParameter): ByteArray {
    return Keccak.digest(encodeToByteArray(), parameter)
}
