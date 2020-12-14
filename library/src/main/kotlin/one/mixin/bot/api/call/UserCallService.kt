package one.mixin.bot.api.call

import one.mixin.bot.api.MixinResponse
import one.mixin.bot.vo.Account
import one.mixin.bot.vo.AccountRequest
import one.mixin.bot.vo.PinRequest
import one.mixin.bot.vo.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserCallService {

    @POST("users")
    fun createUsersCall(@Body request: AccountRequest): Call<MixinResponse<User>>

    @POST("pin/update")
    fun createPinCall(@Body request: PinRequest): Call<MixinResponse<User>>

    @POST("pin/verify")
    fun pinVerifyCall(@Body request: PinRequest): Call<MixinResponse<User>>

    @GET("me")
    fun getMeCall(): Call<MixinResponse<Account>>
}