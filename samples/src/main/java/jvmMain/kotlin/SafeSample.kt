package jvmMain.kotlin

import kotlinx.coroutines.runBlocking
import one.mixin.bot.HttpClient
import one.mixin.bot.encryptPin
import one.mixin.bot.extension.assetIdToAsset
import one.mixin.bot.extension.base64Decode
import one.mixin.bot.extension.base64Encode
import one.mixin.bot.extension.base64UrlDecode
import one.mixin.bot.extension.base64UrlEncode
import one.mixin.bot.extension.hexStringToByteArray
import one.mixin.bot.extension.toHex
import one.mixin.bot.safe.SafeException
import one.mixin.bot.safe.registerSafe
import one.mixin.bot.safe.sendTransaction
import one.mixin.bot.safe.updateTipPin
import one.mixin.bot.util.decryptPinToken
import one.mixin.bot.util.generateEd25519KeyPair
import one.mixin.bot.util.generateRandomBytes
import one.mixin.bot.util.newKeyPairFromSeed
import one.mixin.bot.util.toBeByteArray
import one.mixin.bot.vo.Account
import one.mixin.bot.vo.PinRequest
import one.mixin.bot.vo.safe.MixAddress
import one.mixin.bot.vo.safe.TransactionRecipient
import one.mixin.bot.vo.safe.toMixAddress
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID

fun main() =
    runBlocking {
        val botClient =
            HttpClient.Builder().useCNServer().configSafeUser(
                Config.userId,
                Config.sessionId,
                Config.privateKey.base64UrlDecode(),
                Config.pinTokenPem.base64UrlDecode(),
                Config.pin.hexStringToByteArray(),
            ).build()

        updateFromLegacyPin(botClient)

        // val user = createTipPin(botClient) ?: return@runBlocking

        val currentRelativePath: Path = Paths.get("")
        val s: String = currentRelativePath.toAbsolutePath().toString()
        println("Current absolute path is: $s")
        System.load("$s/library/libs/darwin/amd64/libgojni.so")

        // transactionToOne(botClient)

        // transactionToMultiple(botClient)
    }

private suspend fun updateFromLegacyPin(botClient: HttpClient) {
    // create user
    val sessionKey = generateEd25519KeyPair()
    val sessionSecret = sessionKey.publicKey.base64Encode()
    val user = createUser(botClient, sessionSecret) ?: return

    val userClient =
        HttpClient.Builder().useCNServer().configSafeUser(
            user.userId,
            user.sessionId,
            sessionKey.privateKey,
        ).enableDebug().build()

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
    registerSafe(
        userClient,
        user.userId,
        keyPair.privateKey.toHex(),
        keyPair.privateKey.toHex(),
        userPrivateKey.base64UrlEncode(),
        user.pinToken,
    )
}

private suspend fun createTipPin(botClient: HttpClient): Account? {
    // create user
    val sessionKey = generateEd25519KeyPair()
    val sessionSecret = sessionKey.publicKey.base64Encode()
    val user = createUser(botClient, sessionSecret) ?: return null

    val userClient =
        HttpClient.Builder().useCNServer().configSafeUser(
            user.userId,
            user.sessionId,
            sessionKey.privateKey,
        ).enableDebug().build()

    // decrypt pin token
    val userPrivateKey = sessionKey.privateKey
    val userAesKey = decryptPinToken(user.pinToken.base64Decode(), userPrivateKey)

    // create user tip pin
    val tipSeed = generateRandomBytes(32)
    val keyPair = newKeyPairFromSeed(tipSeed)
    val response =
        userClient.userService.createPin(
            PinRequest(encryptPin(userAesKey, keyPair.publicKey + 1L.toBeByteArray())),
        )
    if (response.isSuccess()) {
        println("Create tip pin success ${response.data?.userId}")
    } else {
        println("Create tip pin failure ${response.error}")
    }

    // register safe
    return registerSafe(
        userClient,
        user.userId,
        keyPair.privateKey.toHex(),
        keyPair.privateKey.toHex(),
        userPrivateKey.base64UrlEncode(),
        user.pinToken,
    )
}

fun transactionToOne(botClient: HttpClient) {
    val asset = assetIdToAsset("965e5c6e-434c-3fa9-b780-c50f43cd955c") // cnb
    val mixAddress =
        MixAddress.newUuidMixAddress(listOf("d3bee23a-81d4-462e-902a-22dae9ef89ff"), 1)
            ?: throw SafeException("newUuidMixAddress got null mixAddress")
    val transactionRecipient = TransactionRecipient(mixAddress, "0.013")
    val trace = UUID.randomUUID().toString()
    println("trace: $trace")
    val tx = sendTransaction(botClient, asset, transactionRecipient, trace, "")
    println(tx)

    try {
        sendTransaction(botClient, asset, transactionRecipient, trace, "")
    } catch (e: SafeException) {
        println("use same id should throw exception ${e.stackTraceToString()}")
    }
}

fun transactionToMultiple(botClient: HttpClient) {
    val asset = "965e5c6e-434c-3fa9-b780-c50f43cd955c" // cnb
    val mixAddress =
        "MIXDLSoouhdcvedoiSzNHNRR4FNqVNwwgHUXkFoApTsz35fBHSNGyZEqGCzWuwDYrrWDwCXiaNcPec4C5cW8tCiE7BUHvs6A9YZ4B6FiFAEYY5Nd1etLA7aE7".toMixAddress()
            ?: throw SafeException("toMixAddress got null mixAddress")
    val transactionRecipient = TransactionRecipient(mixAddress, "0.012")
    val trace = UUID.randomUUID().toString()
    println("trace: $trace")
    val tx = sendTransaction(botClient, asset, transactionRecipient, trace, "")
    println(tx)
}
