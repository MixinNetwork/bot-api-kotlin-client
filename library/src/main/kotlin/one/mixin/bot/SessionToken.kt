package one.mixin.bot

import one.mixin.bot.tip.EdKeyPair
import java.security.Key

sealed class SessionToken(open val userId: String, open val sessionId: String) {
    data class RSA(
        override val userId: String,
        override val sessionId: String,
        val privateKey: Key
    ) : SessionToken(userId, sessionId)

    data class EdDSA(
        override val userId: String,
        override val sessionId: String,
        val keyPair: EdKeyPair
    ) : SessionToken(userId, sessionId)
}