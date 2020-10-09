package one.mixin.bot.api

import one.mixin.bot.vo.Address
import one.mixin.bot.vo.AddressesRequest
import one.mixin.bot.vo.Asset
import one.mixin.bot.vo.Snapshot
import one.mixin.bot.vo.WithdrawalRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AssetService {
  @GET("assets/{id}")
  fun getDeposit(@Path("id") id: String): Call<MixinResponse<Asset>>

  @POST("withdrawals")
  fun withdrawals(@Body request: WithdrawalRequest): Call<MixinResponse<Snapshot>>

  @POST("addresses")
  fun createAddresses(@Body request: AddressesRequest):Call<MixinResponse<Address>>

}
