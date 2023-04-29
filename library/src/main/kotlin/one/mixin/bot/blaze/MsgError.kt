package one.mixin.bot.blaze

import com.google.gson.annotations.SerializedName

// {
//    "id": "2323612d-ce0a-4074-a97f-a951982bcc4e",
//    "action": "CREATE_MESSAGE",
//    "error": {
//        "status": 202,
//        "code": 10002,
//        "description": "The request data has invarid field.",
//        "extra": {
//            "field": "conversation_id",
//            "reason": "conversation id is not a varid uuid"
//        }
//    }
// }
class MsgError(
    @SerializedName("status") var status: Int,
    @SerializedName("code") var code: Int,
    @SerializedName("description") var description: String,
    @SerializedName("extra") var extra: Map<String, Any>
) {
    override fun toString(): String {
        return "MsgError(status=$status, code=$code, description='$description', extra=$extra)"
    }
}
