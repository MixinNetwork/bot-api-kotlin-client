package one.mixin.bot.safe

import one.mixin.bot.HttpClient
import one.mixin.bot.encryptPin
import one.mixin.bot.encryptTipPin
import one.mixin.bot.extension.base64Decode
import one.mixin.bot.extension.base64UrlDecode
import one.mixin.bot.extension.base64UrlEncode
import one.mixin.bot.extension.hexStringToByteArray
import one.mixin.bot.extension.toHex
import one.mixin.bot.util.decryptPinToken
import one.mixin.bot.util.initFromSeedAndSign
import one.mixin.bot.util.newKeyPairFromSeed
import one.mixin.bot.util.sha3Sum256
import one.mixin.bot.util.toBeByteArray
import one.mixin.bot.vo.Account
import one.mixin.bot.vo.PinRequest
import one.mixin.bot.vo.RegisterRequest
import java.io.IOException
import kotlin.jvm.Throws

@Throws(TipException::class, IOException::class)
fun updateTipPin(
    client: HttpClient,
    tipPubHex: String,
    sessionKeyBase64: String,
    pinTokenBase64: String,
    legacyPin: String,
): Account {
    val pinToken = decryptPinToken(pinTokenBase64.base64Decode(), sessionKeyBase64.base64UrlDecode())
    val now = System.currentTimeMillis() * 1_000_000
    val encryptedLegacyPin = encryptPin(pinToken, legacyPin.toByteArray(), now)
    val tipPub = tipPubHex.hexStringToByteArray()
    if (tipPub.size != 32) {
        throw TipException("Invalid tip pub size ${tipPub.size}")
    }
    val newEncryptedPin = encryptPin(pinToken, tipPub + 1L.toBeByteArray(), now + 1)
    val pinRequest = PinRequest(newEncryptedPin, encryptedLegacyPin)
    val resp = client.userService.updatePinCall(pinRequest).execute().body()
    if (resp == null || !resp.isSuccess()) {
        throw TipException("Update pin failed ${resp?.error}")
    }
    return resp.data as Account
}

@Throws(TipException::class, IOException::class)
fun registerSafe(
    client: HttpClient,
    userId: String,
    safeSeedHex: String,
    tipPinHex: String,
    sessionKeyBase64: String,
    pinTokenBase64: String,
): Account {
    val pinToken = decryptPinToken(pinTokenBase64.base64Decode(), sessionKeyBase64.base64UrlDecode())
    val seed = safeSeedHex.hexStringToByteArray()
    val keyPair = newKeyPairFromSeed(seed)
    val safePkHex = keyPair.publicKey.toHex()
    val userIdHash = userId.sha3Sum256()
    val signature = initFromSeedAndSign(seed, userIdHash).base64UrlEncode()

    val tipBody = TipBody.forSequencerRegister(userId, safePkHex)
    val tipPriv = tipPinHex.hexStringToByteArray()
    val bodySig = encryptTipPin(pinToken, tipBody, tipPriv)

    val meResp = client.userService.getMeCall().execute().body()
    if (meResp == null || !meResp.isSuccess()) {
        throw TipException("get safe/me failed ${meResp?.error}")
    }
    val account = meResp.data as Account
    if (account.hasSafe) {
        println("account has registered safe")
        return account
    }

    val request =
        RegisterRequest(
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
