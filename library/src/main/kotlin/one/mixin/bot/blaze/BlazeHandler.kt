package one.mixin.bot.blaze

import okhttp3.WebSocket

interface BlazeHandler {
    fun onMessage(webSocket: WebSocket, blazeMsg: BlazeMsg): Boolean
}
