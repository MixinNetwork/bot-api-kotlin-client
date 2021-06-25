package one.mixin.bot.api.coroutine

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.api.SnapshotService
import one.mixin.bot.vo.NetworkSnapshot
import one.mixin.bot.vo.Snapshot
import one.mixin.bot.vo.TransferRequest
import one.mixin.bot.vo.WithdrawalRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SnapshotCoroutineService {

    @POST("withdrawals")
    suspend fun withdrawals(@Body request: WithdrawalRequest): MixinResponse<Snapshot>

    @GET("snapshots/{id}")
    suspend fun getSnapshotById(@Path("id") id: String): MixinResponse<Snapshot>

    @GET("/snapshots/trace/{id}")
    suspend fun getTrace(@Path("id") traceId: String): MixinResponse<Snapshot>

    @POST("transfers")
    suspend fun transfer(@Body request: TransferRequest): MixinResponse<Snapshot>

    @GET("assets/{id}/snapshots")
    suspend fun getSnapshotsByAssetId(
        @Path("id") id: String,
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = SnapshotService.LIMIT
    ): MixinResponse<List<Snapshot>>

    @GET("snapshots")
    suspend fun getAllSnapshots(
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = SnapshotService.LIMIT,
        @Query("opponent") opponent: String? = null
    ): MixinResponse<List<Snapshot>>

    @GET("snapshots")
    suspend fun getSnapshots(
        @Query("asset") assetId: String,
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = SnapshotService.LIMIT,
        @Query("opponent") opponent: String? = null,
        @Query("destination") destination: String? = null,
        @Query("tag") tag: String? = null
    ): MixinResponse<List<Snapshot>>

    @GET("/network/snapshots/{id}")
    suspend fun networkSnapshot(@Path("id") snapshotId: String): MixinResponse<NetworkSnapshot>

    @GET("/network/snapshots/")
    suspend fun networkSnapshots(
        @Query("asset") assetId: String,
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = SnapshotService.LIMIT,
        @Query("order") order: String? = null
    ): MixinResponse<List<NetworkSnapshot>>
}
