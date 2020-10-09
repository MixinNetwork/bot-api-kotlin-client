package one.mixin.library

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import okhttp3.Request
import one.mixin.library.extension.bodyToString
import one.mixin.library.extension.cutOut
import one.mixin.library.extension.sha256
import one.mixin.library.util.aesEncrypt
import one.mixin.library.extension.toHex
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
                put("sig", content.sha256().toHex())
                put("scp", "FULL")
            }
        )
        .signWith( key)
        .compact()
}

fun encryptPin(pinIterator: PinIterator, key: String, code: String?): String? {
    val pinCode = code ?: return null
    val based = aesEncrypt(key, pinIterator.getValue(), pinCode)
    pinIterator.increment()
    return based
}