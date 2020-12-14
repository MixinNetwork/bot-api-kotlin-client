package one.mixin.bot.api

import one.mixin.bot.api.call.AssetCallService
import one.mixin.bot.api.coroutine.AssetCoroutineService

interface AssetService : AssetCallService, AssetCoroutineService
