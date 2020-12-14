package one.mixin.bot.api.call

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.vo.Address
import one.mixin.bot.vo.AddressRequest
import one.mixin.bot.vo.Asset
import one.mixin.bot.vo.Ticker
import one.mixin.bot.vo.TopAsset
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AssetCallService {

    @GET("assets")
    fun assetsCall(): Call<MixinResponse<List<Asset>>>

    @GET("assets/{id}")
    fun getAssetCall(@Path("id") id: String): Call<MixinResponse<Asset>>

    @GET("/ticker")
    fun tickerCall(
        @Query("asset") assetId: String,
        @Query("offset") offset: String? = null
    ): Call<MixinResponse<Ticker>>

    @POST("addresses")
    fun createAddressesCall(@Body request: AddressRequest): Call<MixinResponse<Address>>

    @GET("network/assets/search/{query}")
    fun queryAssetsCall(@Path("query") query: String): Call<MixinResponse<List<Asset>>>

    @GET("network/assets/top")
    fun topAssetsCall(): Call<MixinResponse<List<TopAsset>>>
}
