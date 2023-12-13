package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class AttachmentResponse(
    @SerializedName("attachment_id")
    val attachmentId: String,
    @SerializedName("upload_url")
    val uploadUrl: String?,
    @SerializedName("view_url")
    val viewUrl: String?,
    @SerializedName("created_at")
    val createdAt: String,
)
