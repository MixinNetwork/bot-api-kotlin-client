package one.mixin.bot.blaze

import com.google.gson.annotations.SerializedName

// {
//    "id": "71639e3f-561c-4cbb-b995-4996a72b46b3",
//    "action": "CREATE_MESSAGE",
//    "data": {
//        "type": "message",
//        "representative_id": "",
//        "quote_message_id": "",
//        "conversation_id": "534d23a7-9ee5-38a0-9769-0f20f83521f6",
//        "user_id": "53bac64e-e1ff-408a-ad0e-aca20cdc68bd",
//        "session_id": "6ab63de1-7483-497c-8916-35d1249ce9c0",
//        "message_id": "f381aaa0-a462-4333-af10-e3f5bff7bec7",
//        "category": "PLAIN_TEXT",
//        "data": "YWJj",
//        "data_base64": "YWJj",
//        "status": "SENT",
//        "source": "CREATE_MESSAGE",
//        "silent": false,
//        "expire_in": 0,
//        "created_at": "2023-04-28T10:01:32.387507Z",
//        "updated_at": "2023-04-28T10:01:32.387507Z"
//    }
// }
data class MsgData constructor(
    @SerializedName("type") var type: String,
    @SerializedName("representative_id") var representativeId: String,
    @SerializedName("quote_message_id") var quoteMessageId: String,
    @SerializedName("conversation_id") var conversionId: String,
    @SerializedName("user_id") var userId: String,
    @SerializedName("session_id") var sessionId: String,
    @SerializedName("message_id") var messageId: String,
    @SerializedName("category") var category: String,
    @SerializedName("data") var data: String,
    @SerializedName("data_base64") var dataBase64: String,
    @SerializedName("status") var status: String,
    @SerializedName("source") var source: String,
    @SerializedName("silent") var silent: Boolean,
    @SerializedName("expire_in") var expireIn: Int,
    @SerializedName("created_at") var createdAt: String,
    @SerializedName("updated_at") var updatedAt: String,
) {
    override fun toString(): String {
        return "MsgData(type='$type', representativeId='$representativeId', quoteMessageId='$quoteMessageId', conversionId='$conversionId', userId='$userId', sessionId='$sessionId', messageId='$messageId', category='$category', data='$data', dataBase64='$dataBase64', status='$status', source='$source', silent=$silent, expireIn=$expireIn, createdAt='$createdAt', updatedAt='$updatedAt')"
    }
}
