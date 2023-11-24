package one.mixin.bot.api

import one.mixin.bot.api.call.UserCallService
import one.mixin.bot.api.coroutine.UserCoroutineService

interface UtxoService : UserCallService, UserCoroutineService
