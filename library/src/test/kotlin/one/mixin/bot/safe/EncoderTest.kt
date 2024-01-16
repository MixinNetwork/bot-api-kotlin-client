package one.mixin.bot.safe

import one.mixin.bot.safe.tx.Encoder
import kotlin.test.Test
import kotlin.test.expect

class EncoderTest {

    @Test
    fun `encode sig`() {
        val encoder = Encoder()
        encoder.encodeSignature(
            mapOf(
                0 to "fde63b999d519394b3ba8a99a9f1d44bc91c2ee73d472d2085fa222925732889159042b126b4436766d6d3308ee541c50fc1b9b8b83701ac534c68e7f4d0f50c"
            )
        )
        expect("00010000fde63b999d519394b3ba8a99a9f1d44bc91c2ee73d472d2085fa222925732889159042b126b4436766d6d3308ee541c50fc1b9b8b83701ac534c68e7f4d0f50c") {
            encoder.toHexString()
        }
    }

}