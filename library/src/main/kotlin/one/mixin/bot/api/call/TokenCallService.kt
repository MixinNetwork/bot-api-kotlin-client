package one.mixin.bot.api.call

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

interface TokenCallService {

    @GET("safe/assets")
    fun fetchAllTokenCall(): Call<MixinResponse<List<Token>>>

    @POST("safe/assets/fetch")
    fun fetchTokenCall(
        @Body id: List<String>,
    ): Call<MixinResponse<List<Token>>>

    @GET("safe/assets/{id}")
    fun getAssetByIdCall(
        @Path("id") id: String,
    ): Call<MixinResponse<Token>>

    @GET("safe/assets/{id}")
    fun getAssetPrecisionByIdCall(
        @Path("id") id: String,
    ): Call<MixinResponse<AssetPrecision>>

    @GET("safe/assets/{id}/fees")
    fun getFeesCall(
        @Path("id") id: String,
        @Query("destination") destination: String,
    ): Call<MixinResponse<List<WithdrawalResponse>>>

    @GET("safe/snapshots")
    fun getSnapshotsByAssetIdCall(
        @Query("asset") id: String,
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = LIMIT,
    ): Call<MixinResponse<List<SafeSnapshot>>>

    @GET("safe/snapshots")
    fun getAllSnapshotsCall(
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = LIMIT,
        @Query("opponent") opponent: String? = null,
    ): Call<MixinResponse<List<SafeSnapshot>>>

    @GET("safe/snapshots")
    fun getSnapshotsCall(
        @Query("asset") assetId: String,
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = LIMIT,
        @Query("opponent") opponent: String? = null,
        @Query("destination") destination: String? = null,
        @Query("tag") tag: String? = null,
    ): Call<MixinResponse<List<SafeSnapshot>>>

    @GET("safe/snapshots/{id}")
    fun getSnapshotByIdCall(
        @Path("id") id: String,
    ): Call<MixinResponse<SafeSnapshot>>

    @GET("safe/deposits")
    fun pendingDepositsCall(
        @Query("asset") asset: String,
        @Query("destination") key: String,
        @Query("tag") tag: String? = null,
    ): Call<MixinResponse<List<PendingDeposit>>>

    @GET("network/assets/search/{query}")
    fun queryAssetsCall(
        @Path("query") query: String,
    ): Call<MixinResponse<List<Token>>>

    @GET("network/assets/top")
    fun topAssetsCall(
        @Query("kind") kind: String = "NORMAL",
        @Query("limit") limit: Int = 100,
    ): Call<Call<MixinResponse<List<TopAsset>>>>

    @GET("network/ticker")
    fun tickerCall(
        @Query("asset") assetId: String,
        @Query("offset") offset: String? = null,
    ): Call<MixinResponse<Ticker>>

    @GET("network/chains")
    fun getChainsCall(): Call<MixinResponse<List<Chain>>>

    @GET("network/chains/{id}")
    fun getChainByIdCall(
        @Path("id") id: String,
    ): Call<MixinResponse<Chain>>
}