package one.mixin.bot.api.coroutine

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.vo.MessageRequest
import one.mixin.bot.vo.Pin
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface MessageCoroutineService {
    @POST("messages")
    suspend fun postMessage(@Body messageRequests: List<MessageRequest>): MixinResponse<Unit>
}