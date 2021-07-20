package one.mixin.bot

import one.mixin.bot.util.aesGcmDecrypt
import one.mixin.bot.util.aesGcmEncrypt
import one.mixin.bot.util.generateAesKey
import kotlin.test.Test
import kotlin.test.assertEquals

class CryptoTest {
    @Test fun testAesGcm() {
        val plain = "test"
        val key = generateAesKey()
        val cipher = aesGcmEncrypt(plain.toByteArray(), key)
        println(cipher.contentToString())
        val p = aesGcmDecrypt(cipher, key)
        println(String(p))
        assertEquals(plain, String(p))
    }
}
