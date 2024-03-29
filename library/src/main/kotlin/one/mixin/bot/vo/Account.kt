package one.mixin.bot.vo

import com.google.gson.annotations.SerializedName

data class Account(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("session_id")
    val sessionId: String,
    val type: String,
    @SerializedName("identity_number")
    val identityNumber: String,
    val relationship: String,
    @SerializedName("full_name")
    val fullName: String?,
    var biography: String?,
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    var phone: String,
    @SerializedName("avatar_base64")
    val avatarBase64: String?,
    @SerializedName("pin_token")
    var pinToken: String,
    @SerializedName("code_id")
    val codeId: String,
    @SerializedName("code_url")
    val codeUrl: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("receive_message_source")
    val receiveMessageSource: String,
    @SerializedName("has_pin")
    val hasPin: Boolean,
    @SerializedName("accept_conversation_source")
    val acceptConversationSource: String,
    @SerializedName("accept_search_source")
    val acceptSearchSource: String,
    @SerializedName("has_emergency_contact")
    var hasEmergencyContact: Boolean,
    @SerializedName("fiat_currency")
    var fiatCurrency: String,
    @SerializedName("transfer_notification_threshold")
    val transferNotificationThreshold: Double = 0.0,
    @SerializedName("transfer_confirmation_threshold")
    val transferConfirmationThreshold: Double = 100.0,
    @SerializedName("tip_key_base64")
    val tipKeyBase64: String,
    @SerializedName("tip_counter")
    val tipCounter: Int,
    @SerializedName("has_safe")
    var hasSafe: Boolean,
)
