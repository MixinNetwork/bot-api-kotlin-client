package one.mixin.bot.api

import one.mixin.bot.vo.Address
import one.mixin.bot.vo.AddressRequest
import one.mixin.bot.vo.Asset
import one.mixin.bot.vo.PendingDeposit
import one.mixin.bot.vo.Ticker
import one.mixin.bot.vo.TopAsset
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AssetService {
    @GET("assets")
    suspend fun assets(): MixinResponse<List<Asset>>

    @GET("assets")
    fun assetsCall(): Call<MixinResponse<List<Asset>>>

    @GET("assets/{id}")
    suspend fun getAsset(@Path("id") id: String): MixinResponse<Asset>

    @GET("assets/{id}")
    fun getAssetCall(@Path("id") id: String): Call<MixinResponse<Asset>>

    @POST("addresses")
    fun createAddressesCall(@Body request: AddressRequest): Call<MixinResponse<Address>>

    @GET("external/transactions")
    suspend fun pendingDeposits(
        @Query("asset") asset: String,
        @Query("destination") key: String? = null,
        @Query("tag") tag: String? = null
    ): MixinResponse<List<PendingDeposit>>

    @GET("network/assets/search/{query}")
    suspend fun queryAssets(@Path("query") query: String): MixinResponse<List<Asset>>

    @GET("network/assets/search/{query}")
    fun queryAssetsCall(@Path("query") query: String): Call<MixinResponse<List<Asset>>>

    @GET("network/assets/top")
    fun topAssetsCall(): Call<MixinResponse<List<TopAsset>>>

    @GET("/ticker")
    suspend fun ticker(
        @Query("asset") assetId: String,
        @Query("offset") offset: String? = null
    ): MixinResponse<Ticker>

    @GET("/ticker")
    fun tickerCall(
        @Query("asset") assetId: String,
        @Query("offset") offset: String? = null
    ): Call<MixinResponse<Ticker>>
}
