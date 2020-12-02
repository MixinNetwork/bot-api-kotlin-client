package one.mixin.example

import kotlinx.coroutines.runBlocking
import one.mixin.bot.HttpClient
import one.mixin.bot.TokenInfo
import one.mixin.bot.encryptPin
import one.mixin.bot.util.Base64
import one.mixin.bot.util.generateRSAKeyPair
import one.mixin.bot.util.rsaDecrypt
import one.mixin.bot.vo.AccountRequest
import one.mixin.bot.vo.AddressesRequest
import one.mixin.bot.vo.PinRequest
import one.mixin.bot.vo.TransferRequest
import one.mixin.bot.vo.WithdrawalRequest
import one.mixin.example.Config.pin
import one.mixin.example.Config.pinToken
import one.mixin.example.Config.privateKey
import one.mixin.example.Config.sessionId
import one.mixin.example.Config.userId
import java.util.Random
import java.util.UUID

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

    // bot transfer to user
    client.setUserToken(null)
    val transferResponse = client.assetService.transfer(
        TransferRequest(
            "965e5c6e-434c-3fa9-b780-c50f43cd955c", user.userId, "1", encryptPin(
                SecretPinIterator(),
                pinToken,
                pin
            )!!
        )
    )
    print(transferResponse.data)
    client.setUserToken(TokenInfo(user.userId, user.sessionId, sessionKey.private))

    // CNB
    val assetResponse = client.assetService.getAsset("965e5c6e-434c-3fa9-b780-c50f43cd955c")
    println(assetResponse.data?.balance)

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
        WithdrawalRequest(addressId,"1", encryptPin(
            SecretPinIterator(),
            userAesKey,
            "131416"
        )!!,UUID.randomUUID().toString(),"withdrawal test")
    )
    println(withdrawalsResponse.data)
}