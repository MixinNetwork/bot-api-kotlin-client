package one.mixin.bot.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import okhttp3.Request
import okio.ByteString.Companion.encode
import one.mixin.bot.PinIterator
import one.mixin.bot.extension.bodyToString
import one.mixin.bot.extension.cutOut
import java.security.Key
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

fun signToken(userId: String, sessionId: String, request: Request, key: Key): String {
    val expire = System.currentTimeMillis() / 1000 + 1800
    val iat = System.currentTimeMillis() / 1000

    var content = "${request.method}${request.url.cutOut()}"
    request.body?.apply {
        if (contentLength() > 0) {
            content += bodyToString()
        }
    }

    return Jwts.builder()
        .setClaims(
            ConcurrentHashMap<String, Any>().apply {
                put(Claims.ID, UUID.randomUUID().toString())
                put(Claims.EXPIRATION, expire)
                put(Claims.ISSUED_AT, iat)
                put("uid", userId)
                put("sid", sessionId)
                put("sig", content.encode().sha256().hex())
                put("scp", "FULL")
            }
        )
        .signWith(key)
        .compact()
}

fun encryptPin(pinIterator: PinIterator, key: String, pin: String?): String? {
    val pinCode = pin ?: return null
    val based = aesEncrypt(key, pinIterator.getValue(), pinCode)
    pinIterator.increment()
    return based
}
