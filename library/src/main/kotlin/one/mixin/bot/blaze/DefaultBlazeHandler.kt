package one.mixin.bot.blaze

import okhttp3.WebSocket

class DefaultBlazeHandler : BlazeHandler {
    override fun onMessage(webSocket: WebSocket, blazeMsg: BlazeMsg): Boolean {
        println(blazeMsg)
        return true
    }
}
