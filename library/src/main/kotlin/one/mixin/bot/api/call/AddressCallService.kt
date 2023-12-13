package one.mixin.bot.api.call

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.vo.Address
import one.mixin.bot.vo.AddressRequest
import one.mixin.bot.vo.Pin
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AddressCallService {
    @POST("addresses")
    fun createAddressesCall(
        @Body request: AddressRequest,
    ): Call<MixinResponse<Address>>

    @POST("addresses/{id}/delete")
    fun deleteCall(
        @Path("id") id: String,
        @Body pin: Pin,
    ): Call<MixinResponse<Unit>>

    @GET("addresses/{id}")
    fun addressCall(
        @Path("id") id: String,
    ): Call<MixinResponse<Address>>

    @GET("assets/{id}/addresses")
    fun addressesCall(
        @Path("id") id: String,
    ): Call<MixinResponse<List<Address>>>
}
