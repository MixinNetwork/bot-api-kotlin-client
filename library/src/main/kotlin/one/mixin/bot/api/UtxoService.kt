package one.mixin.bot.api

import one.mixin.bot.api.call.UtxoCallService
import one.mixin.bot.api.coroutine.UtxoCoroutineService

interface UtxoService : UtxoCallService, UtxoCoroutineService
