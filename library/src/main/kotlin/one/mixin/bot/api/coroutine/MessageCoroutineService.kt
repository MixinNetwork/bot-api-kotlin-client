package one.mixin.bot.api.coroutine

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.vo.MessageRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface MessageCoroutineService {
    @POST("messages")
    suspend fun postMessage(
        @Body messageRequests: List<MessageRequest>,
    ): MixinResponse<Unit>
}
