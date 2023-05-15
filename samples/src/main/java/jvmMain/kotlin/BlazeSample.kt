package jvmMain.kotlin

import okhttp3.WebSocket
import one.mixin.bot.blaze.BlazeClient
import one.mixin.bot.blaze.BlazeHandler
import one.mixin.bot.blaze.BlazeMsg
import one.mixin.bot.blaze.sendTextMsg
import one.mixin.bot.util.getEdDSAPrivateKeyFromString

fun main() {
    val key = getEdDSAPrivateKeyFromString(Config.privateKey)
    val blazeClient = BlazeClient.Builder()
        .configEdDSA(Config.userId, Config.sessionId, key)
        .enableDebug()
        .enableParseData()
        .enableAutoAck()
        .blazeHandler(MyBlazeHandler())
        .build()
    blazeClient.start()

    System.`in`.read()
}

private class MyBlazeHandler : BlazeHandler {
    override fun onMessage(webSocket: WebSocket, blazeMsg: BlazeMsg): Boolean {
        println(blazeMsg)
        val data = blazeMsg.data
        if (data != null) {
            sendTextMsg(webSocket, data.conversionId, data.userId, "read")
        }
        return true
    }
}
