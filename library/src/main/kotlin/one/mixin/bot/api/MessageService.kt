package one.mixin.bot.api

import com.sun.xml.internal.ws.protocol.soap.MessageCreationException
import one.mixin.bot.api.call.AddressCallService
import one.mixin.bot.api.call.MessageCallService
import one.mixin.bot.api.coroutine.AddressCoroutineService
import one.mixin.bot.api.coroutine.MessageCoroutineService

interface MessageService : MessageCallService, MessageCoroutineService
