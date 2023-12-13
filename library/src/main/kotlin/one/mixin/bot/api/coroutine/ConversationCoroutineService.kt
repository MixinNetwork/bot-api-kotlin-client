package one.mixin.bot.api.coroutine

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.vo.CircleConversation
import one.mixin.bot.vo.ConversationCircleRequest
import one.mixin.bot.vo.ConversationRequest
import one.mixin.bot.vo.ConversationResponse
import one.mixin.bot.vo.DisappearRequest
import one.mixin.bot.vo.ParticipantRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ConversationCoroutineService {
    @POST("conversations")
    suspend fun create(
        @Body request: ConversationRequest,
    ): MixinResponse<ConversationResponse>

    @GET("conversations/{id}")
    suspend fun getConversation(
        @Path("id") id: String,
    ): MixinResponse<ConversationResponse>

    @POST("conversations/{id}/participants/{action}")
    suspend fun participants(
        @Path("id") id: String,
        @Path("action") action: String,
        @Body requests: List<ParticipantRequest>,
    ): MixinResponse<ConversationResponse>

    @POST("conversations/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Body request: ConversationRequest,
    ): MixinResponse<ConversationResponse>

    @POST("conversations/{id}/exit")
    suspend fun exit(
        @Path("id") id: String,
    ): MixinResponse<ConversationResponse>

    @POST("conversations/{code_id}/join")
    suspend fun join(
        @Path("code_id") codeId: String,
    ): MixinResponse<ConversationResponse>

    @POST("conversations/{id}/rotate")
    suspend fun rotate(
        @Path("id") id: String,
    ): MixinResponse<ConversationResponse>

    @POST("conversations/{id}/mute")
    suspend fun mute(
        @Path("id") id: String,
        @Body request: ConversationRequest,
    ): MixinResponse<ConversationResponse>

    @POST("conversations/{id}/circles")
    suspend fun updateCircles(
        @Path("id") id: String,
        @Body requests: List<ConversationCircleRequest>,
    ): MixinResponse<List<CircleConversation>>

    @POST("conversations/{id}/disappear")
    suspend fun disappear(
        @Path("id") id: String,
        @Body request: DisappearRequest,
    ): MixinResponse<ConversationResponse>
}
