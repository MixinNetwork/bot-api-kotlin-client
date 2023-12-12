package jvmMain.kotlin

import kotlinx.coroutines.runBlocking
import one.mixin.bot.HttpClient
import one.mixin.bot.encryptPin
import one.mixin.bot.extension.base64Decode
import one.mixin.bot.extension.base64Encode
import one.mixin.bot.extension.base64UrlDecode
import one.mixin.bot.extension.base64UrlEncode
import one.mixin.bot.extension.toHex
import one.mixin.bot.tip.registerSafe
import one.mixin.bot.tip.updateTipPin
import one.mixin.bot.util.decryptPinToken
import one.mixin.bot.util.generateEd25519KeyPair
import one.mixin.bot.util.generateRandomBytes
import one.mixin.bot.util.newKeyPairFromPrivateKey
import one.mixin.bot.util.newKeyPairFromSeed
import one.mixin.bot.util.toBeByteArray
import one.mixin.bot.vo.PinRequest

fun main() = runBlocking {
    val key = newKeyPairFromPrivateKey(Config.privateKey.base64UrlDecode())
    val botClient = HttpClient.Builder().useCNServer().configEdDSA(Config.userId, Config.sessionId, key).build()

    updateFromLegacyPin(botClient)

    createTipPin(botClient)
}

private suspend fun updateFromLegacyPin(botClient: HttpClient) {
    // create user
    val sessionKey = generateEd25519KeyPair()
    val sessionSecret = sessionKey.publicKey.base64Encode()
    val user = createUser(botClient, sessionSecret) ?: return

    val userClient = HttpClient.Builder().useCNServer().configEdDSA(user.userId, user.sessionId, sessionKey).enableDebug().build()

    // decrypt pin token
    val userPrivateKey = sessionKey.privateKey
    val userAesKey = decryptPinToken(user.pinToken.base64Decode(), userPrivateKey)

    // create user's pin
    createPin(userClient, userAesKey)

    // update tip pin
    val tipSeed = generateRandomBytes(32)
    val keyPair = newKeyPairFromSeed(tipSeed)
    updateTipPin(userClient, keyPair.publicKey.toHex(), userPrivateKey.base64UrlEncode(), user.pinToken, DEFAULT_PIN)

    // register safe
    registerSafe(userClient, user.userId, keyPair.privateKey.toHex(), keyPair.privateKey.toHex(), userPrivateKey.base64UrlEncode(), user.pinToken)
}

private suspend fun createTipPin(botClient: HttpClient) {
    // create user
    val sessionKey = generateEd25519KeyPair()
    val sessionSecret = sessionKey.publicKey.base64Encode()
    val user = createUser(botClient, sessionSecret) ?: return

    val userClient = HttpClient.Builder().useCNServer().configEdDSA(user.userId, user.sessionId, sessionKey).enableDebug().build()

    // decrypt pin token
    val userPrivateKey = sessionKey.privateKey
    val userAesKey = decryptPinToken(user.pinToken.base64Decode(), userPrivateKey)

    // create user tip pin
    val tipSeed = generateRandomBytes(32)
    val keyPair = newKeyPairFromSeed(tipSeed)
    val response = userClient.userService.createPin(
        PinRequest(encryptPin(userAesKey, keyPair.publicKey + 1L.toBeByteArray()))
    )
    if (response.isSuccess()) {
        println("Create tip pin success ${response.data?.userId}")
    } else {
        println("Create tip pin failure ${response.error}")
    }

    // register safe
    registerSafe(userClient, user.userId, keyPair.privateKey.toHex(), keyPair.privateKey.toHex(), userPrivateKey.base64UrlEncode(), user.pinToken)
}