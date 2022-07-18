package one.mixin.bot.vo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CircleConversation(
    @SerializedName("conversation_id")
    val conversationId: String,
    @SerializedName("circle_id")
    val circleId: String,
    @SerializedName("user_id")
    val userId: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @Expose
    @SerializedName("pin_time")
    val pinTime: String?
)

enum class CircleConversationAction { ADD, REMOVE }
