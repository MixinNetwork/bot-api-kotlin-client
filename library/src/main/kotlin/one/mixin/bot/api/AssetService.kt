package one.mixin.bot.api

import one.mixin.bot.vo.Address
import one.mixin.bot.vo.AddressRequest
import one.mixin.bot.vo.Asset
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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
}
