package one.mixin.bot

import net.i2p.crypto.eddsa.EdDSAPrivateKey
import one.mixin.bot.extension.base64Encode
import java.security.Key
import java.security.KeyPair

sealed class SessionToken(open val userId: String, open val sessionId: String) {
    data class RSA(
        override val userId: String,
        override val sessionId: String,
        val privateKey: Key
    ) : SessionToken(userId, sessionId)

    data class EdDSA(
        override val userId: String,
        override val sessionId: String,
        val seed: String
    ) : SessionToken(userId, sessionId)
}

fun getUserSessionToken(userId: String, sessionId: String, sessionKey: KeyPair, isRsa: Boolean = false): SessionToken {
    return if (isRsa) {
        SessionToken.RSA(userId, sessionId, sessionKey.private)
    } else {
        SessionToken.EdDSA(
            userId, sessionId,
            (sessionKey.private as EdDSAPrivateKey).seed.base64Encode()
        )
    }
}
