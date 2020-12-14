package one.mixin.bot.api

import one.mixin.bot.api.call.SnapshotCallService
import one.mixin.bot.api.coroutine.SnapshotCoroutineService

interface SnapshotService : SnapshotCallService, SnapshotCoroutineService {
    companion object {
        const val LIMIT = 30
    }
}
