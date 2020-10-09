package one.mixin.library.api

import one.mixin.library.vo.Address
import one.mixin.library.vo.AddressesRequest
import one.mixin.library.vo.Asset
import one.mixin.library.vo.Snapshot
import one.mixin.library.vo.WithdrawalRequest
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
