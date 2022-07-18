package one.mixin.bot.api.call

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.vo.MessageRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MessageCallService {
    @POST("messages")
    fun postMessageCall(@Body messageRequests: List<MessageRequest>): Call<MixinResponse<Void>>
}
