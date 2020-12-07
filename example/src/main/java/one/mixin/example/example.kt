package one.mixin.example

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.i2p.crypto.eddsa.EdDSAPrivateKey
import net.i2p.crypto.eddsa.EdDSAPublicKey
import one.mixin.bot.HttpClient
import one.mixin.bot.TokenInfo
import one.mixin.bot.extension.base64Decode
import one.mixin.bot.extension.base64Encode
import one.mixin.bot.util.encryptPin
import one.mixin.bot.util.Base64
import one.mixin.bot.util.calculateAgreement
import one.mixin.bot.util.generateEd25519KeyPair
import one.mixin.bot.util.generateRSAKeyPair
import one.mixin.bot.util.rsaDecrypt
import one.mixin.bot.vo.AccountRequest
import one.mixin.bot.vo.AddressesRequest
import one.mixin.bot.vo.PinRequest
import one.mixin.bot.vo.TransferRequest
import one.mixin.bot.vo.User
import one.mixin.bot.vo.WithdrawalRequest
import one.mixin.example.Config.pin
import one.mixin.example.Config.pinToken
import one.mixin.example.Config.privateKey
import one.mixin.example.Config.sessionId
import one.mixin.example.Config.userId
import java.security.KeyPair
import java.util.Random
import java.util.UUID

fun main() = runBlocking {
    val client = HttpClient(userId, sessionId, privateKey,true)
    val response = client.userService.getMe()
    println(response.data?.avatarUrl)

    val secretPin = encryptPin(SecretPinIterator(), pinToken, pin) ?: return@runBlocking
    val verifyResponse = client.userService.pinVerify(PinRequest(secretPin))
    println(verifyResponse.isSuccess)

    // toggle use RSA or EdDSA
    val isRsa = false

    // create user 将用户注册到 Mixin 网络
    val sessionKey = if (isRsa) {
        generateRSAKeyPair()
    } else generateEd25519KeyPair()
    val sessionSecret = if (isRsa) {
        Base64.encodeBytes(sessionKey.public.encoded)
    } else {
        val publicKey = sessionKey.public as EdDSAPublicKey
        publicKey.abyte.base64Encode()
    }
    val userResponse = client.userService.createUsers(
        AccountRequest(
            "User${Random().nextInt(100)}",
            sessionSecret
        )
    )
    println("${userResponse.data?.fullName} ${userResponse.data?.userId}")
    val user = userResponse.data ?: return@runBlocking
    client.setUserToken(getUserToken(user, sessionKey, isRsa))

    // decrypt pin token
    val userAesKey: String = if (isRsa) {
        rsaDecrypt(sessionKey.private, user.sessionId, user.pinToken)
    } else {
        val privateKey = sessionKey.private as EdDSAPrivateKey
        calculateAgreement(user.pinToken.base64Decode(), privateKey).base64Encode()
    }
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

    // bot transfer to user

    client.setUserToken(null)
    val transferResponse = client.assetService.transfer(
        TransferRequest(
            "965e5c6e-434c-3fa9-b780-c50f43cd955c", user.userId, "100", encryptPin(
                SecretPinIterator(),
                pinToken,
                pin
            )!!
        )
    )
    print(transferResponse.data)
    client.setUserToken(getUserToken(user, sessionKey, isRsa))

    // CNB
    delay(5000)
    val assetResponse = client.assetService.getAsset("965e5c6e-434c-3fa9-b780-c50f43cd955c")
    println(assetResponse.data)

    // create address
    val addressResponse = client.assetService.createAddresses(
        AddressesRequest(
            "965e5c6e-434c-3fa9-b780-c50f43cd955c",
            "0x45315C1Fd776AF95898C77829f027AFc578f9C2B",
            "label",
            encryptPin(
                SecretPinIterator(),
                userAesKey,
                "131416"
            )!!
        )
    )
    println(addressResponse.data)

    // withdrawal
    val addressId = requireNotNull(addressResponse.data).addressId
    val withdrawalsResponse = client.assetService.withdrawals(
        WithdrawalRequest(addressId,"100", encryptPin(
            SecretPinIterator(),
            userAesKey,
            "131416"
        )!!,UUID.randomUUID().toString(),"withdrawal test")
    )
    println(withdrawalsResponse.data)
}

@Suppress("SameParameterValue")
private fun getUserToken(user: User, sessionKey: KeyPair, isRsa: Boolean) =
    if (isRsa) {
        TokenInfo.RSA(user.userId, user.sessionId, sessionKey.private)
    } else {
        TokenInfo.EdDSA(user.userId, user.sessionId,
            (sessionKey.private as EdDSAPrivateKey).seed.base64Encode())
    }