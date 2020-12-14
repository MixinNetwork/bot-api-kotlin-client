package one.mixin.bot.api

import one.mixin.bot.vo.Address
import one.mixin.bot.vo.AddressRequest
import one.mixin.bot.vo.Pin
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AddressService {

    @POST("addresses")
    suspend fun createAddresses(@Body request: AddressRequest): MixinResponse<Address>

    @POST("addresses")
    fun createAddressesCall(@Body request: AddressRequest): Call<MixinResponse<Address>>

    @POST("addresses/{id}/delete")
    suspend fun delete(@Path("id") id: String, @Body pin: Pin): MixinResponse<Unit>

    @POST("addresses/{id}/delete")
    suspend fun deleteCall(@Path("id") id: String, @Body pin: Pin): Call<MixinResponse<Unit>>

    @GET("addresses/{id}")
    suspend fun address(@Path("id") id: String): MixinResponse<Address>

    @GET("addresses/{id}")
    fun addressCall(@Path("id") id: String): Call<MixinResponse<Address>>

    @GET("assets/{id}/addresses")
    fun addressesCall(@Path("id") id: String): Call<MixinResponse<List<Address>>>
}
