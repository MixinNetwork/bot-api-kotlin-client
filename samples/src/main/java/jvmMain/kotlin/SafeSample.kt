package jvmMain.kotlin

import kotlinx.coroutines.runBlocking
import one.mixin.bot.HttpClient
import one.mixin.bot.encryptPin
import one.mixin.bot.encryptTipPin
import one.mixin.bot.extension.base64Decode
import one.mixin.bot.extension.base64Encode
import one.mixin.bot.extension.base64UrlEncode
import one.mixin.bot.extension.nowInUtcNano
import one.mixin.bot.extension.toHex
import one.mixin.bot.safe.TipBody
import one.mixin.bot.safe.registerSafe
import one.mixin.bot.safe.updateTipPin
import one.mixin.bot.util.decryptPinToken
import one.mixin.bot.util.generateEd25519KeyPair
import one.mixin.bot.util.generateRandomBytes
import one.mixin.bot.util.newKeyPairFromSeed
import one.mixin.bot.util.toBeByteArray
import one.mixin.bot.vo.Account
import one.mixin.bot.vo.PinRequest

fun main() = runBlocking {
   updateFromLegacyPin(botClient)

   // val user = createTipPin(botClient) ?: return@runBlocking
}

private suspend fun updateFromLegacyPin(botClient: HttpClient) { // create user
   val sessionKey = generateEd25519KeyPair()
   val sessionSecret = sessionKey.publicKey.base64Encode()
   val user = createUser(botClient, sessionSecret) ?: return

   val userClient = HttpClient.Builder().useCNServer().configSafeUser(
       user.userId,
       user.sessionId,
       sessionKey.privateKey,
   ).enableDebug().build()

   // decrypt pin token
   val userPrivateKey = sessionKey.privateKey
   val userAesKey = decryptPinToken(user.pinToken.base64Decode(), userPrivateKey)

   // create user pin
   var response = userClient.userService.createPin(PinRequest(encryptPin(userAesKey, DEFAULT_PIN.toByteArray())))
   if (response.isSuccess()) {
       println("Create pin success ${response.data}")
   } else {
       throw Exception("Create pin failure ${response.error}")
   } // verify usr pin
   response = userClient.userService.pinVerify(PinRequest(encryptPin(userAesKey, DEFAULT_PIN.toByteArray())))
   if (response.isSuccess()) {
       println("Verify pin success")
   } else {
       throw Exception("Verify pin failure ${response.error}")
   }

   // update tip pin
   val tipSeed = generateRandomBytes(32)
   val keyPair = newKeyPairFromSeed(tipSeed)
   updateTipPin(
       userClient,
       keyPair.publicKey.toHex(),
       userPrivateKey.base64UrlEncode(),
       user.pinToken,
       DEFAULT_PIN
   ) // verify tip pin
   val timestamp = nowInUtcNano()
   response = userClient.userService.pinVerify(
       PinRequest(
           encryptTipPin(
               userAesKey,
               TipBody.forVerify(timestamp),
               keyPair.privateKey
           ), timestamp = timestamp
       )
   )
   if (response.isSuccess()) {
       println("Verify tip pin success")
   } else {
       throw Exception("Verify tip pin failure ${response.error}")
   }

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

private suspend fun createTipPin(botClient: HttpClient): Account? { // create user
   val sessionKey = generateEd25519KeyPair()
   val sessionSecret = sessionKey.publicKey.base64Encode()
   val user = createUser(botClient, sessionSecret) ?: return null

   val userClient = HttpClient.Builder().useCNServer().configSafeUser(
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
   val response = userClient.userService.createPin(
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