package one.mixin.bot.api.coroutine

import one.mixin.bot.Constants.LIMIT
import one.mixin.bot.api.MixinResponse
import one.mixin.bot.vo.Chain
import one.mixin.bot.vo.PendingDeposit
import one.mixin.bot.vo.Ticker
import one.mixin.bot.vo.TopAsset
import one.mixin.bot.vo.safe.AssetPrecision
import one.mixin.bot.vo.safe.SafeSnapshot
import one.mixin.bot.vo.safe.Token
import one.mixin.bot.vo.safe.WithdrawalResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TokenCoroutineService {
    @GET("safe/assets")
    suspend fun fetchAllToken(): MixinResponse<List<Token>>

    @POST("safe/assets/fetch")
    suspend fun fetchToken(
        @Body id: List<String>,
    ): MixinResponse<List<Token>>

    @GET("safe/assets/{id}")
    suspend fun getAssetById(
        @Path("id") id: String,
    ): MixinResponse<Token>

    @GET("safe/assets/{id}")
    suspend fun getAssetPrecisionById(
        @Path("id") id: String,
    ): MixinResponse<AssetPrecision>

    @GET("safe/assets/{id}/fees")
    suspend fun getFees(
        @Path("id") id: String,
        @Query("destination") destination: String,
    ): MixinResponse<List<WithdrawalResponse>>

    @GET("safe/snapshots")
    suspend fun getSnapshotsByAssetId(
        @Query("asset") id: String,
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = LIMIT,
    ): MixinResponse<List<SafeSnapshot>>

    @GET("safe/snapshots")
    suspend fun getSnapshotsByAppId(
        @Query("app") id: String,
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = LIMIT,
    ): MixinResponse<List<SafeSnapshot>>

    @GET("safe/snapshots")
    suspend fun getAllSnapshots(
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = LIMIT,
        @Query("opponent") opponent: String? = null,
    ): MixinResponse<List<SafeSnapshot>>

    @GET("safe/snapshots")
    suspend fun getSnapshots(
        @Query("asset") assetId: String,
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = LIMIT,
        @Query("opponent") opponent: String? = null,
        @Query("destination") destination: String? = null,
        @Query("tag") tag: String? = null,
    ): MixinResponse<List<SafeSnapshot>>

    @GET("safe/snapshots/{id}")
    suspend fun getSnapshotById(
        @Path("id") id: String,
    ): MixinResponse<SafeSnapshot>

    @GET("safe/deposits")
    suspend fun pendingDeposits(
        @Query("asset") asset: String,
        @Query("destination") key: String,
        @Query("tag") tag: String? = null,
    ): MixinResponse<List<PendingDeposit>>

    @GET("network/assets/search/{query}")
    suspend fun queryAssets(
        @Path("query") query: String,
    ): MixinResponse<List<Token>>

    @GET("network/assets/top")
    fun topAssets(
        @Query("kind") kind: String = "NORMAL",
        @Query("limit") limit: Int = 100,
    ): Call<MixinResponse<List<TopAsset>>>

    @GET("network/ticker")
    suspend fun ticker(
        @Query("asset") assetId: String,
        @Query("offset") offset: String? = null,
    ): MixinResponse<Ticker>

    @GET("network/chains")
    suspend fun getChains(): MixinResponse<List<Chain>>

    @GET("network/chains/{id}")
    suspend fun getChainById(
        @Path("id") id: String,
    ): MixinResponse<Chain>
}
