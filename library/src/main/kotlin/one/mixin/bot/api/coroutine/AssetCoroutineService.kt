package one.mixin.bot.api.coroutine

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.vo.Asset
import one.mixin.bot.vo.AssetFee
import one.mixin.bot.vo.Fiat
import one.mixin.bot.vo.PendingDeposit
import one.mixin.bot.vo.Ticker
import one.mixin.bot.vo.TopAsset
import one.mixin.bot.vo.TransactionRequest
import one.mixin.bot.vo.TransactionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AssetCoroutineService {
    @GET("assets")
    suspend fun assets(): MixinResponse<List<Asset>>

    @GET("network/ticker")
    suspend fun ticker(
        @Query("asset") assetId: String,
        @Query("offset") offset: String? = null
    ): MixinResponse<Ticker>

    @GET("assets/{id}")
    suspend fun getAsset(@Path("id") id: String): MixinResponse<Asset>

    @GET("external/transactions")
    suspend fun pendingDeposits(
        @Query("asset") asset: String,
        @Query("destination") key: String? = null,
        @Query("tag") tag: String? = null
    ): MixinResponse<List<PendingDeposit>>

    @GET("network/assets/search/{query}")
    suspend fun queryAssets(@Path("query") query: String): MixinResponse<List<Asset>>

    @GET("network/assets/top")
    suspend fun topAssets(): MixinResponse<List<TopAsset>>

    @GET("fiats")
    suspend fun getFiats(): MixinResponse<List<Fiat>>

    @GET("assets/{id}/fee")
    suspend fun assetsFee(@Path("id") id: String): MixinResponse<AssetFee>

    @POST("transactions")
    suspend fun transactions(@Body request: TransactionRequest): MixinResponse<TransactionResponse>
}
