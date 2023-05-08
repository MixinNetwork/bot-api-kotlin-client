package one.mixin.bot.blaze

import com.google.gson.annotations.SerializedName
import one.mixin.bot.api.ResponseError

// {"id":"17ee6406-b65a-483f-9529-72a94b011d7d","action":"LIST_PENDING_MESSAGES"}
// {"id":"71639e3f-561c-4cbb-b995-4996a72b46b3","action":"CREATE_MESSAGE","data":{"type":"message","representative_id":"","quote_message_id":"","conversation_id":"534d23a7-9ee5-38a0-9769-0f20f83521f6","user_id":"53bac64e-e1ff-408a-ad0e-aca20cdc68bd","session_id":"6ab63de1-7483-497c-8916-35d1249ce9c0","message_id":"f381aaa0-a462-4333-af10-e3f5bff7bec7","category":"PLAIN_TEXT","data":"YWJj","data_base64":"YWJj","status":"SENT","source":"CREATE_MESSAGE","silent":false,"expire_in":0,"created_at":"2023-04-28T10:01:32.387507Z","updated_at":"2023-04-28T10:01:32.387507Z"}}
// {"id":"d76f716c-89ca-43f2-8713-c293c958305e","action":"ACKNOWLEDGE_MESSAGE_RECEIPT"}
class BlazeMsg(
    @SerializedName("id") var id: String,
    @SerializedName("action") var action: String,
    @SerializedName("params") var params: MsgParam? = null,
    @SerializedName("data") var data: MsgData? = null,
    @SerializedName("error") var error: ResponseError? = null,
) {
    override fun toString(): String {
        return "BlazeMsg(id='$id', action='$action', params=$params, data=$data, error=$error)"
    }
}
