package one.mixin.bot.api

import one.mixin.bot.api.call.TokenCallService
import one.mixin.bot.api.coroutine.TokenCoroutineService

interface TokenService : TokenCallService, TokenCoroutineService
