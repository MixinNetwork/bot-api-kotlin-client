package one.mixin.bot

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import java.security.Key
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import okhttp3.Request
import okio.ByteString.Companion.encode
import one.mixin.bot.extension.base64Decode
import one.mixin.bot.extension.base64Encode
import one.mixin.bot.extension.bodyToString
import one.mixin.bot.extension.path
import one.mixin.bot.extension.toLeByteArray
import one.mixin.bot.util.aesEncrypt
import one.mixin.bot.util.initFromSeedAndSign

fun signToken(userId: String, sessionId: String, method: String, path: String, body: String?, key: Key): String {
    val expire = System.currentTimeMillis() / 1000 + 1800
    val iat = System.currentTimeMillis() / 1000

    var content = "${method}$path"
    body?.let {
        content += body
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

fun signToken(userId: String, sessionId: String, request: Request, key: Key): String {
    var body: String? = null
    request.body?.apply {
        if (contentLength() > 0) {
            body = bodyToString()
        }
    }
    return signToken(userId, sessionId, request.method, request.url.path(), body, key)
}

@JvmOverloads
fun encryptPin(
    pinToken: String,
    pin: String,
    iterator: Long = System.currentTimeMillis() * 1_000_000,
): String = encryptPin(pinToken, pin.toByteArray(), iterator)

@JvmOverloads
fun encryptPin(
    pinToken: String,
    signTarget: ByteArray,
    iterator: Long = System.currentTimeMillis() * 1_000_000,
): String {
    val pinByte = signTarget + (System.currentTimeMillis() / 1000).toLeByteArray() + iterator.toLeByteArray()
    return aesEncrypt(pinToken.base64Decode(), pinByte).base64Encode()
}

fun encryptTipPin(
    pinToken: String,
    signTarget: ByteArray,
    tipPriv: ByteArray,
    iterator: Long = System.currentTimeMillis() * 1_000_000,
): String {
    val sig = initFromSeedAndSign(tipPriv, signTarget)
    val pinByte = sig + (System.currentTimeMillis() / 1000).toLeByteArray() + iterator.toLeByteArray()
    return aesEncrypt(pinToken.base64Decode(), pinByte).base64Encode()
}
