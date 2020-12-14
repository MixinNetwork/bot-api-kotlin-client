package one.mixin.bot.api.call

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.api.SnapshotService.Companion.LIMIT
import one.mixin.bot.vo.Snapshot
import one.mixin.bot.vo.TransferRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SnapshotCallService {
    @GET("snapshots/{id}")
    fun getSnapshotByIdCall(@Path("id") id: String): Call<MixinResponse<Snapshot>>

    @GET("/snapshots/trace/{id}")
    fun getTraceCall(@Path("id") traceId: String): Call<MixinResponse<Snapshot>>

    @GET("assets/{id}/snapshots")
    fun getSnapshotsByAssetIdCall(
        @Path("id") id: String,
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = LIMIT
    ): Call<MixinResponse<List<Snapshot>>>

    @GET("snapshots")
    fun getAllSnapshotsCall(
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = LIMIT,
        @Query("opponent") opponent: String? = null
    ): Call<MixinResponse<List<Snapshot>>>

    @POST("transfers")
    fun transferCall(@Body request: TransferRequest): Call<MixinResponse<Snapshot>>

    @GET("snapshots")
    fun getSnapshotsCall(
        @Query("asset") assetId: String,
        @Query("offset") offset: String? = null,
        @Query("limit") limit: Int = LIMIT,
        @Query("opponent") opponent: String? = null,
        @Query("destination") destination: String? = null,
        @Query("tag") tag: String? = null
    ): Call<MixinResponse<List<Snapshot>>>
}