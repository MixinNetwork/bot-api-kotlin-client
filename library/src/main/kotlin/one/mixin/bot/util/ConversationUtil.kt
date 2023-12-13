package one.mixin.bot.util

import java.util.UUID

class ConversationUtil {
    companion object {
        fun generateConversationId(
            senderId: String,
            recipientId: String,
        ): String {
            val mix = minOf(senderId, recipientId) + maxOf(senderId, recipientId)
            return UUID.nameUUIDFromBytes(mix.toByteArray()).toString()
        }
    }
}
