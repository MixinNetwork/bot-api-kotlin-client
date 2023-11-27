package one.mixin.bot.api.call

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.vo.tip.TipEphemeral
import one.mixin.bot.vo.tip.TipGas
import one.mixin.bot.vo.tip.TipIdentity
import one.mixin.bot.vo.tip.TipRequest
import one.mixin.bot.vo.tip.TipSecretReadRequest
import one.mixin.bot.vo.tip.TipSecretRequest
import one.mixin.bot.vo.tip.TipSecretResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
interface TipCallService {
    @GET("tip/identity")
    fun tipIdentity(): Call<MixinResponse<TipIdentity>>

    @GET("tip/ephemerals")
    fun tipEphemerals(): Call<MixinResponse<List<TipEphemeral>>>

    @POST("tip/ephemerals")
    fun tipEphemeral(
        @Body request: TipRequest,
    ): Call<MixinResponse<Unit>>

    @POST("tip/secret")
    fun readTipSecret(
        @Body request: TipSecretReadRequest,
    ): Call<MixinResponse<TipSecretResponse>>

    @POST("tip/secret")
    fun updateTipSecret(
        @Body request: TipSecretRequest,
    ): Call<MixinResponse<Unit>>

    @GET("external/gastracker/{id}")
    fun getTipGas(
        @Path("id") assetId: String,
    ): Call<MixinResponse<TipGas>>
}
