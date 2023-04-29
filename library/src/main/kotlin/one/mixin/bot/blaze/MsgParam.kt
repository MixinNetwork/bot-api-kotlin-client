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
data class MsgParam(
    @SerializedName("message_id") var messageId: String,
    @SerializedName("category") var category: String? = null,
    @SerializedName("conversation_id") var conversionId: String? = null,
    @SerializedName("recipient_id") var representativeId: String? = null,
    @SerializedName("data") var data: String? = null,
    @SerializedName("status") var status: String? = null,
) {
    override fun toString(): String {
        return "MsgParam(messageId='$messageId', category=$category, conversionId=$conversionId, representativeId=$representativeId, data=$data, status=$status)"
    }
}
