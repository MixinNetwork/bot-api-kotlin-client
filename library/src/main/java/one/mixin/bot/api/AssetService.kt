package one.mixin.bot.api

import one.mixin.bot.vo.Address
import one.mixin.bot.vo.AddressesRequest
import one.mixin.bot.vo.Asset
import one.mixin.bot.vo.Snapshot
import one.mixin.bot.vo.TransferRequest
import one.mixin.bot.vo.WithdrawalRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AssetService {
    @GET("assets")
    suspend fun assets(): MixinResponse<List<Asset>>

    @GET("assets/{id}")
    suspend fun getAsset(@Path("id") id: String): MixinResponse<Asset>

    @GET("assets/{id}/snapshots")
    suspend fun getSnapshotsByAssetId(
        @Path("id") id: String,
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = 30
    ): MixinResponse<List<Snapshot>>

    @POST("withdrawals")
    suspend fun withdrawals(@Body request: WithdrawalRequest): MixinResponse<Snapshot>

    @POST("addresses")
    suspend fun createAddresses(@Body request: AddressesRequest): MixinResponse<Address>

    @POST("transfers")
    suspend fun transfer(@Body request: TransferRequest): MixinResponse<Snapshot>
}
