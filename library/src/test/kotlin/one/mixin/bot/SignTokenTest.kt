package one.mixin.bot

import com.google.gson.JsonParser
import io.jsonwebtoken.EdDSAPrivateKey
import okio.ByteString.Companion.toByteString
import one.mixin.bot.extension.base64UrlDecode
import one.mixin.bot.util.generateEd25519KeyPair
import kotlin.test.Test
import kotlin.test.assertEquals

class SignTokenTest {
    @Test fun testTokenExpiresFiveMinutesAfterIssue() {
        val key = EdDSAPrivateKey(generateEd25519KeyPair().privateKey.toByteString())
        val token = signToken("uid", "sid", "GET", "/me", null, key)
        val claims = JsonParser.parseString(String(token.split(".")[1].base64UrlDecode())).asJsonObject
        assertEquals(300L, claims["exp"].asLong - claims["iat"].asLong)
    }
}
