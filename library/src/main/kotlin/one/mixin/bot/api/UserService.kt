package one.mixin.bot.api

import one.mixin.bot.api.call.UserCallService
import one.mixin.bot.api.coroutine.UserCoroutineService
import one.mixin.bot.vo.Account
import one.mixin.bot.vo.AccountRequest
import one.mixin.bot.vo.PinRequest
import one.mixin.bot.vo.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService : UserCallService, UserCoroutineService