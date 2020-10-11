package one.mixin.example

import kotlinx.coroutines.runBlocking
import one.mixin.bot.HttpClient
import one.mixin.bot.encryptPin
import one.mixin.bot.vo.PinRequest
import one.mixin.example.Config.pin
import one.mixin.example.Config.pinToken
import one.mixin.example.Config.privateKey
import one.mixin.example.Config.sessionId
import one.mixin.example.Config.userId

fun main() = runBlocking {
    val client = HttpClient(userId, sessionId, privateKey)
    val response = client.userService.getMe()
    print(response.data?.avatarUrl)
    val c = encryptPin(SecretPinIterator(), pinToken, pin)
    if (c != null) {
        val verifyResponse = client.userService.pinVerify(PinRequest(c))
        print(verifyResponse.data?.avatarURL)
    }
}