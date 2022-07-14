package one.mixin.bot.api

import one.mixin.bot.api.call.AttachmentCallService
import one.mixin.bot.api.coroutine.AttachmentCoroutineService

interface AttachmentService : AttachmentCallService, AttachmentCoroutineService
