package one.mixin.bot.api.call

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.vo.CircleConversation
import one.mixin.bot.vo.ConversationCircleRequest
import one.mixin.bot.vo.ConversationRequest
import one.mixin.bot.vo.ConversationResponse
import one.mixin.bot.vo.DisappearRequest
import one.mixin.bot.vo.ParticipantRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ConversationCallService {

    @POST("conversations")
    fun createCall(@Body request: ConversationRequest): Call<MixinResponse<ConversationResponse>>

    @GET("conversations/{id}")
    fun getConversationCall(@Path("id") id: String): Call<MixinResponse<ConversationResponse>>

    @POST("conversations/{id}/participants/{action}")
    fun participantsCall(
        @Path("id") id: String,
        @Path("action") action: String,
        @Body requests: List<ParticipantRequest>
    ): Call<MixinResponse<ConversationResponse>>

    @POST("conversations/{id}")
    fun updateCall(@Path("id") id: String, @Body request: ConversationRequest): Call<MixinResponse<ConversationResponse>>

    @POST("conversations/{id}/exit")
    fun exitCall(@Path("id") id: String): Call<MixinResponse<ConversationResponse>>

    @POST("conversations/{code_id}/join")
    fun joinCall(@Path("code_id") codeId: String): Call<MixinResponse<ConversationResponse>>

    @POST("conversations/{id}/rotate")
    fun rotateCall(@Path("id") id: String): Call<MixinResponse<ConversationResponse>>

    @POST("conversations/{id}/mute")
    fun muteCall(@Path("id") id: String, @Body request: ConversationRequest): Call<MixinResponse<ConversationResponse>>

    @POST("conversations/{id}/circles")
    fun updateCirclesCall(@Path("id") id: String, @Body requests: List<ConversationCircleRequest>): Call<MixinResponse<List<CircleConversation>>>

    @POST("conversations/{id}/disappear")
    fun disappearCall(@Path("id") id: String, @Body request: DisappearRequest): Call<MixinResponse<ConversationResponse>>
}
