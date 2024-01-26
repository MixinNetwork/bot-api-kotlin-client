package jvmMain.kotlin

import one.mixin.bot.HttpClient
import one.mixin.bot.api.MixinResponse
import one.mixin.bot.extension.hexStringToByteArray

val botClient = HttpClient.Builder().configSafeUser(
    userId = Config.BOT_USER_ID,
    sessionId = Config.BOT_SESSION_ID,
    sessionPrivateKey = Config.BOT_SESSION_PRIVATE_KEY.hexStringToByteArray(),
    spendPrivateKey = Config.BOT_SPEND_KEY.hexStringToByteArray(),
    serverPublicKey = Config.BOT_SERVER_PUBLIC_KEY.hexStringToByteArray(),
).enableDebug().build()


enum class Token(val assetId: String) {
    CNB("965e5c6e-434c-3fa9-b780-c50f43cd955c"),

    TRON_USDT("b91e18ff-a9ae-3dc7-8679-e935d9a4b34b"),

    TRX("25dabac5-056a-48ff-b9f9-f67395dc407c"),
}
