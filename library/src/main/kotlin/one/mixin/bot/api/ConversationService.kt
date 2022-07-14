package one.mixin.bot.api

import one.mixin.bot.api.call.ConversationCallService
import one.mixin.bot.api.coroutine.ConversationCoroutineService

interface ConversationService : ConversationCallService, ConversationCoroutineService
