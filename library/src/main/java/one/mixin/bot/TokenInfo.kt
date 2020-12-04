package one.mixin.bot

import java.security.Key

sealed class TokenInfo(open val userId: String, open val sessionId: String) {
    data class RSA(
        override val userId: String,
        override val sessionId: String,
        val privateKey: Key
    ) : TokenInfo(userId, sessionId)

    data class EdDSA(
        override val userId: String,
        override val sessionId: String,
        val seed: String
    ) : TokenInfo(userId, sessionId)
}