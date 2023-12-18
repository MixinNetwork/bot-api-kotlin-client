package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName
import one.mixin.bot.extension.base64Encode
import one.mixin.bot.util.ConversationUtil.Companion.generateConversationId

data class MessageRequest(
    @SerializedName("conversation_id")
    val conversationId: String,
    @SerializedName("recipient_id")
    val recipientId: String,
    @SerializedName("message_id")
    val messageId: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("data")
    val data: String,
    @SerializedName("representative_id")
    val representativeId: String? = null,
    @SerializedName("quote_message_id")
    val quoteMessageId: String? = null,
)

fun generateTextMessageRequest(
    senderId: String,
    recipientId: String,
    messageId: String,
    text: String,
    representativeId: String? = null,
    quoteMessageId: String? = null,
) = MessageRequest(
    generateConversationId(senderId, recipientId),
    recipientId,
    messageId,
    "PLAIN_TEXT",
    requireNotNull(text.toByteArray().base64Encode()),
    representativeId,
    quoteMessageId,
)
