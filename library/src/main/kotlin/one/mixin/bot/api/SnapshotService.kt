package one.mixin.bot.api

import one.mixin.bot.vo.Snapshot
import one.mixin.bot.vo.TransferRequest
import one.mixin.bot.vo.WithdrawalRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SnapshotService {

    @POST("withdrawals")
    suspend fun withdrawals(@Body request: WithdrawalRequest): MixinResponse<Snapshot>

    @POST("withdrawals")
    fun withdrawalsCall(@Body request: WithdrawalRequest): Call<MixinResponse<Snapshot>>

    @GET("snapshots/{id}")
    suspend fun getSnapshotById(@Path("id") id: String): MixinResponse<Snapshot>

    @GET("snapshots/{id}")
    fun getSnapshotByIdCall(@Path("id") id: String): Call<MixinResponse<Snapshot>>

    @GET("/snapshots/trace/{id}")
    suspend fun getTrace(@Path("id") traceId: String): MixinResponse<Snapshot>

    @GET("/snapshots/trace/{id}")
    fun getTraceCall(@Path("id") traceId: String): Call<MixinResponse<Snapshot>>

    @POST("transfers")
    suspend fun transfer(@Body request: TransferRequest): MixinResponse<Snapshot>

    @POST("transfers")
    fun transferCall(@Body request: TransferRequest): Call<MixinResponse<Snapshot>>

    @GET("assets/{id}/snapshots")
    suspend fun getSnapshotsByAssetId(
        @Path("id") id: String,
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = LIMIT
    ): MixinResponse<List<Snapshot>>

    @GET("assets/{id}/snapshots")
    fun getSnapshotsByAssetIdCall(
        @Path("id") id: String,
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = LIMIT
    ): Call<MixinResponse<List<Snapshot>>>

    @GET("snapshots")
    suspend fun getAllSnapshots(
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = LIMIT,
        @Query("opponent") opponent: String? = null
    ): MixinResponse<List<Snapshot>>

    @GET("snapshots")
    fun getAllSnapshotsCall(
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = LIMIT,
        @Query("opponent") opponent: String? = null
    ): Call<MixinResponse<List<Snapshot>>>

    @GET("snapshots")
    suspend fun getSnapshots(
        @Query("asset") assetId: String,
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = LIMIT,
        @Query("opponent") opponent: String? = null,
        @Query("destination") destination: String? = null,
        @Query("tag") tag: String? = null
    ): MixinResponse<List<Snapshot>>

    @GET("snapshots")
    fun getSnapshotsCall(
        @Query("asset") assetId: String,
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = LIMIT,
        @Query("opponent") opponent: String? = null,
        @Query("destination") destination: String? = null,
        @Query("tag") tag: String? = null
    ): Call<MixinResponse<List<Snapshot>>>

    companion object {
        const val LIMIT = 30
    }
}
