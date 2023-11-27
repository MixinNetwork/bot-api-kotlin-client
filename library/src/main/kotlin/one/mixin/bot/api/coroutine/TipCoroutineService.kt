package one.mixin.bot.api.coroutine

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.vo.tip.TipEphemeral
import one.mixin.bot.vo.tip.TipGas
import one.mixin.bot.vo.tip.TipIdentity
import one.mixin.bot.vo.tip.TipRequest
import one.mixin.bot.vo.tip.TipSecretReadRequest
import one.mixin.bot.vo.tip.TipSecretRequest
import one.mixin.bot.vo.tip.TipSecretResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TipCoroutineService {
    @GET("tip/identity")
    suspend fun tipIdentity(): MixinResponse<TipIdentity>

    @GET("tip/ephemerals")
    suspend fun tipEphemerals(): MixinResponse<List<TipEphemeral>>

    @POST("tip/ephemerals")
    suspend fun tipEphemeral(
        @Body request: TipRequest,
    ): MixinResponse<Unit>

    @POST("tip/secret")
    suspend fun readTipSecret(
        @Body request: TipSecretReadRequest,
    ): MixinResponse<TipSecretResponse>

    @POST("tip/secret")
    suspend fun updateTipSecret(
        @Body request: TipSecretRequest,
    ): MixinResponse<Unit>

    @GET("external/gastracker/{id}")
    suspend fun getTipGas(
        @Path("id") assetId: String,
    ): MixinResponse<TipGas>
}
