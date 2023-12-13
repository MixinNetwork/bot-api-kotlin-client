package one.mixin.bot.api.call

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.vo.AttachmentResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AttachmentCallService {
    @POST("attachments")
    suspend fun requestAttachmentCall(): Call<MixinResponse<AttachmentResponse>>

    @GET("attachments/{id}")
    suspend fun getAttachmentCall(
        @Path("id") id: String,
    ): Call<MixinResponse<AttachmentResponse>>
}
