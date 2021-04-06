package one.mixin.bot.api

import one.mixin.bot.api.call.MessageCallService
import one.mixin.bot.api.coroutine.MessageCoroutineService

interface MessageService : MessageCallService, MessageCoroutineService
