package jvmMain.kotlin

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.WebSocket
import one.mixin.bot.blaze.BlazeClient
import one.mixin.bot.blaze.BlazeHandler
import one.mixin.bot.blaze.BlazeMsg
import one.mixin.bot.blaze.sendTextMsg
import one.mixin.bot.extension.base64Decode
import one.mixin.bot.extension.hexStringToByteArray
import one.mixin.bot.util.newKeyPairFromPrivateKey
import one.mixin.bot.util.newKeyPairFromSeed

fun main(): Unit = runBlocking {
    val job = launch {
        val keyPair = newKeyPairFromSeed(Config.BOT_SESSION_PRIVATE_KEY.hexStringToByteArray())
        val blazeClient =
            BlazeClient.Builder().configSafeUser(Config.BOT_USER_ID, Config.BOT_SESSION_ID, keyPair.privateKey)
                .enableDebug().enableParseData().enableAutoAck().blazeHandler(MyBlazeHandler()).build()
        blazeClient.start()
    }
    job.join()
}

private class MyBlazeHandler : BlazeHandler {
    override fun onMessage(
        webSocket: WebSocket,
        blazeMsg: BlazeMsg,
    ): Boolean {
        println(blazeMsg)
        blazeMsg.data?.let { data ->
            sendTextMsg(webSocket, data.conversionId, data.userId, "read")
        }
        return true
    }
}
