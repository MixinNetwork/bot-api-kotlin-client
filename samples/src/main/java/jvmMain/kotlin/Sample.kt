package jvmMain.kotlin

import jvmMain.kotlin.Config.pin
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.i2p.crypto.eddsa.EdDSAPrivateKey
import net.i2p.crypto.eddsa.EdDSAPublicKey
import one.mixin.bot.HttpClient
import one.mixin.bot.SessionToken
import one.mixin.bot.encryptPin
import one.mixin.bot.extension.base64Decode
import one.mixin.bot.extension.base64Encode
import one.mixin.bot.util.calculateAgreement
import one.mixin.bot.util.decryASEKey
import one.mixin.bot.util.generateEd25519KeyPair
import one.mixin.bot.util.getEdDSAPrivateKeyFromString
import one.mixin.bot.vo.AccountRequest
import one.mixin.bot.vo.AddressRequest
import one.mixin.bot.vo.PinRequest
import one.mixin.bot.vo.TransactionRequest
import one.mixin.bot.vo.TransferRequest
import one.mixin.bot.vo.User
import one.mixin.bot.vo.WithdrawalRequest
import one.mixin.bot.vo.generateTextMessageRequest
import java.util.Random
import java.util.UUID

const val CNB_ID = "965e5c6e-434c-3fa9-b780-c50f43cd955c"
const val BTC_ID = "c6d0c728-2624-429b-8e0d-d9d19b6592fa"
const val DEFAULT_PIN = "131416"
const val DEFAULT_AMOUNT = "0.01"

fun main() = runBlocking {
    val key = getEdDSAPrivateKeyFromString(Config.privateKey)
    val pinToken = decryASEKey(Config.pinTokenPem, key) ?: return@runBlocking
    val client =
        HttpClient.Builder().useCNServer().configEdDSA(Config.userId, Config.sessionId, key).build()

    val sessionKey = generateEd25519KeyPair()
    val publicKey = sessionKey.public as EdDSAPublicKey
    val sessionSecret = publicKey.abyte.base64Encode()

    // create user
    val user = createUser(client, sessionSecret)
    user ?: return@runBlocking
    client.setUserToken(
        SessionToken.EdDSA(
            user.userId, user.sessionId,
            (sessionKey.private as EdDSAPrivateKey).seed.base64Encode()
        )
    )

    // decrypt pin token
    val userAesKey: String
    val userPrivateKey = sessionKey.private as EdDSAPrivateKey
    userAesKey = calculateAgreement(user.pinToken.base64Decode(), userPrivateKey).base64Encode()

    // create user's pin
    createPin(client, userAesKey)
    //Use bot's token

    //Use bot's token
    client.setUserToken(null)
    // bot transfer to user
    transferToUser(client, user.userId, pinToken, pin)

    delay(2000)
    // Use user's token
    client.setUserToken(
        SessionToken.EdDSA(
            user.userId,
            user.sessionId,
            userPrivateKey.seed.base64Encode()
        )
    )

    // Get ticker
    getTicker(client)

    // Get fiats
    getFiats(client)

    // Get BTC fee
    getFee(client)

    // Get asset
    getAsset(client)

    // Create address
    val addressId = createAddress(client, userAesKey) ?: return@runBlocking

    // Withdrawal
    withdrawalToAddress(client, addressId, userAesKey)

    //Use bot's token
    client.setUserToken(null)
    // Send text message
    sendTextMessage(client, "639ec50a-d4f1-4135-8624-3c71189dcdcc", "Text message")

    // Transactions
    transactions(client, pinToken)

    networkSnapshots(client, CNB_ID)
    networkSnapshot(client, "c8e73a02-b543-4100-bd7a-879ed4accdfc")
    return@runBlocking
}

private suspend fun createUser(client: HttpClient, sessionSecret: String): User? {
    val response = client.userService.createUsers(
        AccountRequest(
            Random().nextInt(10).toString() + "User",
            sessionSecret
        )
    )
    return response.data
}

private suspend fun createPin(client: HttpClient, userAesKey: String) {
    val response = client.userService.createPin(
        PinRequest(encryptPin(userAesKey, DEFAULT_PIN))
    )
    if (response.isSuccess()) {
        println("Create pin success ${response.data?.userId}")
    } else {
        println("Create pin fail")
    }
}

private suspend fun transferToUser(
    client: HttpClient,
    userId: String,
    aseKey: String,
    pin: String
) {
    val response = client.snapshotService.transfer(
        TransferRequest(
            CNB_ID,
            userId,
            DEFAULT_AMOUNT,
            encryptPin(aseKey, pin, System.nanoTime())
        )
    )
    if (response.isSuccess()) {
        println("Transfer success: ${response.data?.snapshotId}")
    } else {
        println("Transfer fail ${response.error}")
    }
}

private suspend fun getAsset(client: HttpClient) {
    // Get asset
    val assetResponse = client.assetService.getAsset(CNB_ID)
    if (assetResponse.isSuccess()) {
        println("Assets ${assetResponse.data?.symbol}: ${assetResponse.data?.balance}")
    } else {
        println("Assets fail")
    }
}

private suspend fun getFiats(client: HttpClient) {
    // Get fiats
    val fiatsResponse = client.assetService.getFiats()
    if (fiatsResponse.isSuccess()) {
        println("Fiats ${fiatsResponse.data?.get(0)?.code}: ${fiatsResponse.data?.get(0)?.rate}")
    } else {
        println("Fiats fail")
    }
}

private suspend fun getFee(client: HttpClient) {
    // Get fee
    val feeResponse = client.assetService.assetsFee(BTC_ID)
    if (feeResponse.isSuccess()) {
        println("Fee ${feeResponse.data?.amount}")
    } else {
        println("Fee fail")
    }
}

private suspend fun getTicker(client: HttpClient) {
    // Get fee
    val tickerResponse = client.assetService.ticker(BTC_ID)
    if (tickerResponse.isSuccess()) {
        println("Ticker ${tickerResponse.data}")
    } else {
        println("Ticker fail")
    }
}

private suspend fun createAddress(client: HttpClient, userAesKey: String): String? {
    // Create address
    val addressesResponse = client.addressService.createAddresses(
        AddressRequest(
            CNB_ID,
            "0x45315C1Fd776AF95898C77829f027AFc578f9C2B",
            null,
            "label",
            requireNotNull(
                encryptPin(
                    userAesKey,
                    DEFAULT_PIN,
                    System.nanoTime()
                )
            )
        )
    )

    if (addressesResponse.isSuccess()) {
        println("Create address ${addressesResponse.data?.addressId}")
    } else {
        println("Assets fail")
    }
    return addressesResponse.data?.addressId
}

private suspend fun withdrawalToAddress(
    client: HttpClient,
    addressId: String,
    userAesKey: String
) {
    // Withdrawals
    val withdrawalsResponse = client.snapshotService.withdrawals(
        WithdrawalRequest(
            addressId, DEFAULT_AMOUNT, requireNotNull(
                encryptPin(
                    userAesKey,
                    DEFAULT_PIN,
                    System.nanoTime()
                )
            ), UUID.randomUUID().toString(), "withdrawal test"
        )
    )
    if (withdrawalsResponse.isSuccess()) {
        println("Withdrawal success: ${withdrawalsResponse.data?.snapshotId}")
    } else {
        println("Withdrawal fail")
    }
}

private suspend fun sendTextMessage(client: HttpClient, recipientId: String, text: String) {
    val response = client.messageService.postMessage(
        listOf(
            generateTextMessageRequest(
                Config.userId,
                recipientId,
                UUID.randomUUID().toString(),
                text
            )
        )
    )
    if (response.isSuccess()) {
        println("Send success")
    } else {
        println("Send fail")
    }
}

private suspend fun transactions(
    client: HttpClient,
    userAesKey: String,
) {
    // Transactions
    val transactionsResponse = client.assetService.transactions(
        TransactionRequest(
            CNB_ID,
            // OpponentMultisig(listOf("00c5a4ae-dcdc-48db-ab8e-a7eef69b441d", "087e91ff-7169-451a-aaaa-5b3297411a4b", "4e0e6e6b-6c9d-4e99-b7f1-1356322abec3"), 2),
            opponentMultisig = null,
            opponentKey = "XINQTmRReDuPEUAVEyDyE2mBgxa1ojVRAvpYcKs5nSA7FDBBfAEeVRn8s9vAm3Cn1qzQ7JtjG62go4jSJU6yWyRUKHpamWAM", // test address
            DEFAULT_AMOUNT,
            encryptPin(
                userAesKey,
                pin,
                System.nanoTime()
            ),
            UUID.randomUUID().toString(),
            "memo"
        )
    )
    if (transactionsResponse.isSuccess()) {
        println("Transactions success: ${transactionsResponse.data?.snapshotId}")
    } else {
        println("Transactions fail")
    }
}

private suspend fun networkSnapshot(
    client: HttpClient,
    snapshotId: String
) {
    val snapshotResponse = client.snapshotService.networkSnapshot(snapshotId)
    if (snapshotResponse.isSuccess()) {
        println("Success: ${snapshotResponse.data?.snapshotId}")
    } else {
        println("Fail")
    }
}

private suspend fun networkSnapshots(
    client: HttpClient,
    assetId: String
) {
    val snapshotResponse = client.snapshotService.networkSnapshots(assetId)
    if (snapshotResponse.isSuccess()) {
        println("Success: ${snapshotResponse.data?.size}")
    } else {
        println("Fail: ${snapshotResponse.error?.description}")
    }
}