package one.mixin.bot.api

import one.mixin.bot.api.call.AddressCallService
import one.mixin.bot.api.coroutine.AddressCoroutineService

interface AddressService : AddressCallService, AddressCoroutineService
