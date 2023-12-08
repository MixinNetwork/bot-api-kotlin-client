@file:OptIn(ExperimentalStdlibApi::class)

package one.mixin.bot.tip

import one.mixin.bot.HttpClient
import one.mixin.bot.encryptPin
import one.mixin.bot.encryptTipPin
import one.mixin.bot.util.initFromSeedAndSign
import one.mixin.bot.util.newKeyPairFromSeed
import one.mixin.bot.util.sha3Sum256
import one.mixin.bot.util.toBeByteArray
import one.mixin.bot.vo.Account
import one.mixin.bot.vo.PinRequest
import one.mixin.bot.vo.RegisterRequest

fun updateTipPin(client: HttpClient, tipPubHex: String, pinTokenBase64: String, pin: String) {
    val encryptedPin = encryptPin(pinTokenBase64, pin.toByteArray())
    val tipPub = tipPubHex.hexToByteArray()
    if (tipPub.size != 32) {
        throw TipException("Invalid tip pub size ${tipPub.size}")
    }
    val newEncryptedPin = encryptPin(pinTokenBase64, tipPub + 1L.toBeByteArray())
    val pinRequest = PinRequest(newEncryptedPin, encryptedPin)
    val resp = client.userService.updatePinCall(pinRequest).execute().body()
    if (resp == null || !resp.isSuccess()) {
        throw TipException("Update pin failed")
    }
    val account = resp.data as Account
    println("account updated to tip. tipPub: ${account.tipKeyBase64}, hasSafe: ${account.hasSafe}")
}

fun registerSafe(client: HttpClient, userId: String, safeSeed: String, tipPriv: ByteArray, pinTokenBase64: String): Account {
    val seed = safeSeed.hexToByteArray()
    val keyPair = newKeyPairFromSeed(seed)
    val safePkHex = keyPair.publicKey.toHexString()
    val userIdHash = userId.sha3Sum256()
    val signature = initFromSeedAndSign(keyPair.privateKey, userIdHash).toHexString()

    val tipBody = TipBody.forSequencerRegister(userId, safePkHex)
    val bodySig = encryptTipPin(pinTokenBase64, tipBody, tipPriv)

    val meResp = client.userService.getMeCall().execute().body()
    if (meResp == null || !meResp.isSuccess()) {
        throw TipException("get safe/me failed ${meResp?.error}")
    }
    val account =  meResp.data as Account
    if (account.hasSafe) {
        println("account has registered safe")
        return account
    }

    val request = RegisterRequest(
        publicKey = safePkHex,
        signature = signature,
        pin = bodySig,
    )
    val resp = client.utxoService.registerPublicKeyCall(request).execute().body()
    if (resp == null || !resp.isSuccess()) {
        throw TipException("register safe failed ${resp?.error}")
    }
    return resp.data as Account
}