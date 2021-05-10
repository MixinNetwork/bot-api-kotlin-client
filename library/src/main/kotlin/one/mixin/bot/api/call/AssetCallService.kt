package one.mixin.bot.api.call

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.api.SnapshotService
import one.mixin.bot.vo.Asset
import one.mixin.bot.vo.AssetFee
import one.mixin.bot.vo.Fiat
import one.mixin.bot.vo.MultisigsRequest
import one.mixin.bot.vo.MultisigsResponse
import one.mixin.bot.vo.OutPuts
import one.mixin.bot.vo.PendingDeposit
import one.mixin.bot.vo.Ticker
import one.mixin.bot.vo.TopAsset
import one.mixin.bot.vo.TransactionRequest
import one.mixin.bot.vo.TransactionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AssetCallService {

    @GET("assets")
    fun assetsCall(): Call<MixinResponse<List<Asset>>>

    @GET("/ticker")
    fun tickerCall(
        @Query("asset") assetId: String,
        @Query("offset") offset: String? = null
    ): Call<MixinResponse<Ticker>>

    @GET("assets/{id}")
    fun getAssetCall(@Path("id") id: String): Call<MixinResponse<Asset>>

    @GET("external/transactions")
    fun pendingDepositsCall(
        @Query("asset") asset: String,
        @Query("destination") key: String? = null,
        @Query("tag") tag: String? = null
    ): Call<MixinResponse<List<PendingDeposit>>>

    @GET("network/assets/search/{query}")
    fun queryAssetsCall(@Path("query") query: String): Call<MixinResponse<List<Asset>>>

    @GET("network/assets/top")
    fun topAssetsCall(): Call<MixinResponse<List<TopAsset>>>

    @GET("fiats")
    fun getFiatsCall(): Call<MixinResponse<List<Fiat>>>

    @GET("assets/{id}/fee")
    fun assetsFeeCall(@Path("id") id: String): Call<MixinResponse<AssetFee>>

    @POST("transactions")
    fun transactionsCall(@Body request: TransactionRequest): Call<MixinResponse<TransactionResponse>>

    @GET("multisigs/outputs")
    fun outputs(
        @Query("members") members: String? = null,
        @Query("threshold") threshold: Int? = null,
        @Query("state") state: Int? = null,
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = SnapshotService.LIMIT
    ): Call<MixinResponse<OutPuts>>

    @POST("multisigs/requests")
    fun requests(@Body request: MultisigsRequest): Call<MixinResponse<MultisigsResponse>>

    @POST("multisigs/requests/:id/:action")
    fun requests(
        @Path("id") id: String,
        @Path("action") action: String,
        @Body pin: String
    ): Call<MixinResponse<MultisigsResponse>>
}
