@file:OptIn(ExperimentalStdlibApi::class)

package jvmMain.kotlin

import jvmMain.kotlin.Config.pin
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import one.mixin.bot.HttpClient
import one.mixin.bot.SessionToken
import one.mixin.bot.api.SnapshotService
import one.mixin.bot.encryptPin
import one.mixin.bot.extension.base64Decode
import one.mixin.bot.extension.base64Encode
import one.mixin.bot.util.base64Encode
import one.mixin.bot.util.calculateAgreement
import one.mixin.bot.util.decryASEKey
import one.mixin.bot.util.generateEd25519KeyPair
import one.mixin.bot.util.newKeyPairFromPrivateKey
import one.mixin.bot.util.privateKeyToCurve25519
import one.mixin.bot.vo.AccountRequest
import one.mixin.bot.vo.AddressRequest
import one.mixin.bot.vo.ConversationRequest
import one.mixin.bot.vo.GhostKeyRequest
import one.mixin.bot.vo.MessageRequest
import one.mixin.bot.vo.NetworkSnapshot
import one.mixin.bot.vo.ParticipantRequest
import one.mixin.bot.vo.PinRequest
import one.mixin.bot.vo.Snapshot
import one.mixin.bot.vo.TransactionRequest
import one.mixin.bot.vo.TransferRequest
import one.mixin.bot.vo.User
import one.mixin.bot.vo.WithdrawalRequest
import one.mixin.bot.vo.generateTextMessageRequest
import java.util.Random
import java.util.UUID

const val CNB_ID = "965e5c6e-434c-3fa9-b780-c50f43cd955c"
const val BTC_ID = "c6d0c728-2624-429b-8e0d-d9d19b6592fa"
const val DEFAULT_PIN = "5011c07b101e07b74667398d57a40e9001aa8f6c13fe0836a07a1b5f7cf71e4e"
const val DEFAULT_AMOUNT = "0.01"

fun main() = runBlocking {
    val key = newKeyPairFromPrivateKey(Config.privateKey.base64Decode())
    val pinToken = decryASEKey(Config.pinTokenPem, key.privateKey) ?: return@runBlocking
    val client =
        HttpClient.Builder().useCNServer().configEdDSA(Config.userId, Config.sessionId, key).build()

    val sessionKey = generateEd25519KeyPair()
    val sessionSecret = sessionKey.publicKey.base64Encode()

    // create user
    val user = createUser(client, sessionSecret)
    user ?: return@runBlocking
    val userToken = SessionToken.EdDSA(
        user.userId, user.sessionId,
        sessionKey,
    )
    client.setUserToken(userToken)

    // decrypt pin token
    val userAesKey: String
    val userPrivateKey = sessionKey.privateKey
    userAesKey = calculateAgreement(user.pinToken.base64Decode(), privateKeyToCurve25519(userPrivateKey)).base64Encode()

    // create user's pin
    createPin(client, userAesKey)

    // Use bot's token
    client.setUserToken(null)
    // bot transfer to user
    transferToUser(client, user.userId, pinToken, pin)

    delay(2000)
    // Use user's token
    client.setUserToken(
        SessionToken.EdDSA(
            user.userId,
            user.sessionId,
            sessionKey,
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
    val addressId = createAddress(client, userAesKey)
    if (addressId != null) {
        // Withdrawal
        withdrawalToAddress(client, addressId, userAesKey)
    }

    // Use bot's token
    client.setUserToken(null)
    // Send text message
    sendTextMessage(client, "639ec50a-d4f1-4135-8624-3c71189dcdcc", "Text message")

    createConversationAndSendMessage(client, Config.userId)

    // Transactions
    transactions(client, pinToken)

    networkSnapshots(client, CNB_ID)
    networkSnapshot(client, "c8e73a02-b543-4100-bd7a-879ed4accdfc")

    readGhostKey(client)
    return@runBlocking
}

internal suspend fun createUser(client: HttpClient, sessionSecret: String): User? {
    val response = client.userService.createUsers(
        AccountRequest(
            Random().nextInt(10).toString() + "User",
            sessionSecret
        )
    )
    return response.data
}

internal suspend fun createPin(client: HttpClient, userAesKey: String) {
    val response = client.userService.createPin(
        PinRequest(encryptPin(userAesKey, DEFAULT_PIN.hexToByteArray()))
    )
    if (response.isSuccess()) {
        println("Create pin success ${response.data?.userId}")
    } else {
        println("Create pin failure ${response.error}")
    }
}

internal suspend fun transferToUser(
    client: HttpClient,
    userId: String,
    aseKey: String,
    pin: String
): Snapshot? {
    val response = client.snapshotService.transfer(
        TransferRequest(
            CNB_ID,
            userId,
            DEFAULT_AMOUNT,
            encryptPin(aseKey, pin.toByteArray())
        )
    )
    var snapshot: Snapshot? = null
    if (response.isSuccess()) {
        snapshot = response.data
        println("Transfer success: ${response.data?.snapshotId}")
    } else {
        println("Transfer failure ${response.error}")
    }
    return snapshot
}

private suspend fun getAsset(client: HttpClient) {
    // Get asset
    val assetResponse = client.assetService.getAsset(BTC_ID)
    if (assetResponse.isSuccess()) {
        println("Assets ${assetResponse.data?.symbol}: ${assetResponse.data?.balance} ${assetResponse.data?.depositEntries?.last()?.properties}")
    } else {
        println("Assets failure ${assetResponse.error}")
    }
}

private suspend fun getFiats(client: HttpClient) {
    // Get fiats
    val fiatsResponse = client.assetService.getFiats()
    if (fiatsResponse.isSuccess()) {
        println("Fiats ${fiatsResponse.data?.get(0)?.code}: ${fiatsResponse.data?.get(0)?.rate}")
    } else {
        println("Fiats failure ${fiatsResponse.error}")
    }
}

private suspend fun getFee(client: HttpClient) {
    // Get fee
    val feeResponse = client.assetService.assetsFee(BTC_ID)
    if (feeResponse.isSuccess()) {
        println("Fee ${feeResponse.data?.amount}")
    } else {
        println("Fee failure ${feeResponse.error}")
    }
}

private suspend fun getTicker(client: HttpClient) {
    // Get fee
    val tickerResponse = client.assetService.ticker(BTC_ID)
    if (tickerResponse.isSuccess()) {
        println("Ticker ${tickerResponse.data}")
    } else {
        println("Ticker failure ${tickerResponse.error}")
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
            encryptPin(
                userAesKey,
                DEFAULT_PIN,
            )
        )
    )

    if (addressesResponse.isSuccess()) {
        println("Create address ${addressesResponse.data?.addressId}")
    } else {
        println("Create address failure ${addressesResponse.error}")
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
            addressId, DEFAULT_AMOUNT,
            encryptPin(
                userAesKey,
                DEFAULT_PIN,
            ),
            UUID.randomUUID().toString(), "withdrawal test"
        )
    )
    if (withdrawalsResponse.isSuccess()) {
        println("Withdrawal success: ${withdrawalsResponse.data?.snapshotId}")
    } else {
        println("Withdrawal failure ${withdrawalsResponse.error}")
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
        println("Send failure ${response.error}")
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
            ),
            UUID.randomUUID().toString(),
            "memo"
        )
    )
    if (transactionsResponse.isSuccess()) {
        println("Transactions success: ${transactionsResponse.data?.snapshotId}")
    } else {
        println("Transactions failure ${transactionsResponse.error}")
    }
}

internal suspend fun networkSnapshot(
    client: HttpClient,
    snapshotId: String
) {
    val snapshotResponse = client.snapshotService.networkSnapshot(snapshotId)
    if (snapshotResponse.isSuccess()) {
        println("Network snapshot success: ${snapshotResponse.data?.snapshotId}")
    } else {
        println("Network snapshot failure ${snapshotResponse.error}")
    }
}

internal suspend fun networkSnapshots(
    client: HttpClient,
    assetId: String,
    offset: String? = null,
    limit: Int = SnapshotService.LIMIT,
    order: String? = null
): List<NetworkSnapshot>? {
    val snapshotResponse = client.snapshotService.networkSnapshots(assetId, offset, limit, order)
    var networkSnapshots: List<NetworkSnapshot>? = null
    if (snapshotResponse.isSuccess()) {
        networkSnapshots = snapshotResponse.data as List<NetworkSnapshot>
        println("Network snapshots success")
        for (s in networkSnapshots) {
            println(s)
        }
    } else {
        println("Network snapshot failure: ${snapshotResponse.error?.description}")
    }
    return networkSnapshots
}

private suspend fun readGhostKey(client: HttpClient) {
    val request = GhostKeyRequest(
        listOf(
            "639ec50a-d4f1-4135-8624-3c71189dcdcc",
            "d3bee23a-81d4-462e-902a-22dae9ef89ff",
        ),
        0, ""
    )
    val response = client.userService.readGhostKeys(request)
    if (response.isSuccess()) {
        println("ReadGhostKey success ${response.data}")
    } else {
        println("ReadGhostKey failure ${response.error}")
    }
}

internal suspend fun createConversationAndSendMessage(client: HttpClient, botUserId: String) {
    client.setUserToken(null)
    val botParticipant = ParticipantRequest(
        userId = botUserId,
        role = "",
    )
    val userParticipant = ParticipantRequest(
        userId = "e26808d4-b31f-4e3b-9521-19e529b967b0",
        role = "",
    )
    val conversationRequest = ConversationRequest(
        conversationId = UUID.randomUUID().toString(),
        category = "GROUP",
        name = "test group",
        participants = listOf(botParticipant, userParticipant),
    )
    val conversationResponse = client.conversationService.create(conversationRequest)
    if (conversationResponse.isSuccess()) {
        println("create conversation success ${conversationResponse.data}")
    } else {
        println("create conversation failure ${conversationResponse.error}")
        return
    }

    val conversation = conversationResponse.data ?: return
    val messageRequest = MessageRequest(
        conversationId = conversation.conversationId,
        recipientId = UUID.randomUUID().toString(),
        messageId = UUID.randomUUID().toString(),
        category = "PLAIN_TEXT",
        data = requireNotNull(base64Encode("hello from bot".toByteArray())),
    )
    println("messageRequest: $messageRequest")
    val messageResponse = client.messageService.postMessage(listOf(messageRequest))
    if (messageResponse.isSuccess()) {
        println("Bot send message success")
    } else {
        println("Bot send message failure ${messageResponse.error}")
    }
}
