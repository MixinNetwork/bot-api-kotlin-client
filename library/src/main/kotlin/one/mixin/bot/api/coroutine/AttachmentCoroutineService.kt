package one.mixin.bot.api.coroutine

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.vo.AttachmentResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AttachmentCoroutineService {
    @POST("attachments")
    suspend fun requestAttachment(): MixinResponse<AttachmentResponse>

    @GET("attachments/{id}")
    suspend fun getAttachment(
        @Path("id") id: String,
    ): MixinResponse<AttachmentResponse>
}
