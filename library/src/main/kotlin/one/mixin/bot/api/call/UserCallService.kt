package one.mixin.bot.api.call

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.vo.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserCallService {

    @POST("users")
    fun createUsersCall(@Body request: AccountRequest): Call<MixinResponse<User>>

    @POST("pin/update")
    fun createPinCall(@Body request: PinRequest): Call<MixinResponse<User>>

    @POST("pin/verify")
    fun pinVerifyCall(@Body request: PinRequest): Call<MixinResponse<User>>

    @POST("pin/update")
    fun updatePinCall(@Body request: PinRequest): Call<MixinResponse<Account>>

    @GET("me")
    fun getMeCall(): Call<MixinResponse<Account>>

    @POST("multisigs/{id}/cancel")
    fun cancelMultisigsCall(@Path("id") id: String): Call<MixinResponse<Void>>

    @POST("multisigs/{id}/sign")
    fun signMultisigsCall(
        @Path("id") id: String,
        @Body pinRequest: PinRequest
    ): Call<MixinResponse<Void>>

    @POST("multisigs/{id}/unlock")
    fun unlockMultisigsCall(
        @Path("id") id: String,
        @Body pinRequest: PinRequest
    ): Call<MixinResponse<Void>>

    @POST("outputs")
    fun readGhostKeysCall(
        @Body ghostKeyRequest: GhostKeyRequest
    ): MixinResponse<List<GhostKey>>
}
