package one.mixin.bot.util.keccak

/**
 * Parameters defining the FIPS 202 standard.
 */
public enum class KeccakParameter constructor(public val rateInBytes: Int, public val outputLengthInBytes: Int, public val d: Int) {
    KECCAK_224(144, 28, 0x01),
    KECCAK_256(136, 32, 0x01),
    KECCAK_384(104, 48, 0x01),
    KECCAK_512(72, 64, 0x01),

    SHA3_224(144, 28, 0x06),
    SHA3_256(136, 32, 0x06),
    SHA3_384(104, 48, 0x06),
    SHA3_512(72, 64, 0x06),

    SHAKE128(168, 32, 0x1F),
    SHAKE256(136, 64, 0x1F),
}
