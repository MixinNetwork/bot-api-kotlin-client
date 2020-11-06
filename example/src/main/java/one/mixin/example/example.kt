package one.mixin.example

import kotlinx.coroutines.runBlocking
import one.mixin.bot.HttpClient
import one.mixin.bot.TokenInfo
import one.mixin.bot.encryptPin
import one.mixin.bot.util.Base64
import one.mixin.bot.util.generateRSAKeyPair
import one.mixin.bot.util.rsaDecrypt
import one.mixin.bot.vo.AccountRequest
import one.mixin.bot.vo.PinRequest
import one.mixin.example.Config.privateKey
import one.mixin.example.Config.sessionId
import one.mixin.example.Config.userId
import java.util.Random

fun main() = runBlocking {
    val client = HttpClient(userId, sessionId, privateKey, true)
    val response = client.userService.getMe()
    println(response.data?.avatarUrl)

    // val secretPin = encryptPin(SecretPinIterator(), pinToken, pin) ?: return@runBlocking
    // val verifyResponse = client.userService.pinVerify(PinRequest(secretPin))
    // println(verifyResponse.isSuccess)

    // create user 将用户注册到 Mixin 网络
    val sessionKey = generateRSAKeyPair()
    val sessionSecret = Base64.encodeBytes(sessionKey.public.encoded)
    val userResponse = client.userService.createUsers(
        AccountRequest(
            "User${Random().nextInt(100)}",
            sessionSecret
        )
    )
    println("${userResponse.data?.fullName} ${userResponse.data?.userId}")
    val user = userResponse.data ?: return@runBlocking
    client.setUserToken(TokenInfo(user.userId, user.sessionId, sessionKey.private))
    // decrypt pin token
    val userAesKey: String =
        rsaDecrypt(sessionKey.private, user.sessionId, user.pinToken)
    val pinResponse = client.userService.createPin(
        PinRequest(
            encryptPin(
                SecretPinIterator(),
                userAesKey,
                "131416"
            )!!
        )
    )
    println(pinResponse.isSuccess)

    val assetResponse = client.assetService.assets()
    println(assetResponse.data)
}