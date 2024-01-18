package jvmMain.kotlin

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import one.mixin.bot.HttpClient
import one.mixin.bot.encryptPin
import one.mixin.bot.extension.base64Decode
import one.mixin.bot.extension.base64Encode
import one.mixin.bot.util.decryptPinToken
import one.mixin.bot.util.generateEd25519KeyPair
import one.mixin.bot.vo.AccountRequest
import one.mixin.bot.vo.ConversationRequest
import one.mixin.bot.vo.MessageRequest
import one.mixin.bot.vo.ParticipantRequest
import one.mixin.bot.vo.PinRequest
import one.mixin.bot.vo.User
import one.mixin.bot.vo.generateTextMessageRequest
import java.util.Random
import java.util.UUID

const val DEFAULT_PIN = "131416"
fun main() = runBlocking {
    // create user
    val sessionKey = generateEd25519KeyPair()
    val sessionSecret = sessionKey.publicKey.base64Encode()
    val user = createUser(botClient, sessionSecret)
    user ?: return@runBlocking
    val userClient = HttpClient.Builder().configSafeUser(
        user.userId,
        user.sessionId,
        sessionKey.privateKey,
    ).enableDebug().build()
    userClient.userService.getMe()

    // decrypt pin token
    val userPrivateKey = sessionKey.privateKey
    val userAesKey = decryptPinToken(user.pinToken.base64Decode(), userPrivateKey)

    // create user's pin
    createPin(userClient, userAesKey)

    delay(2000)

    // Send text message
    sendTextMessage(botClient, "639ec50a-d4f1-4135-8624-3c71189dcdcc", "Text message")

    createConversationAndSendMessage(botClient, Config.BOT_USER_ID)

    return@runBlocking
}

internal suspend fun createUser(
    client: HttpClient,
    sessionSecret: String,
): User? {
    val response = client.userService.createUsers(
        AccountRequest(
            Random().nextInt(10).toString() + "User",
            sessionSecret,
        ),
    )
    return response.data
}

internal suspend fun createPin(
    client: HttpClient,
    userAesKey: ByteArray,
) {
    val response = client.userService.createPin(
        PinRequest(encryptPin(userAesKey, DEFAULT_PIN.toByteArray())),
    )
    if (response.isSuccess()) {
        println("Create pin success ${response.data?.userId}")
    } else {
        println("Create pin failure ${response.error}")
    }
}

private suspend fun sendTextMessage(
    client: HttpClient,
    recipientId: String,
    text: String,
) {
    val response = client.messageService.postMessage(
        listOf(
            generateTextMessageRequest(
                Config.BOT_USER_ID,
                recipientId,
                UUID.randomUUID().toString(),
                text,
            ),
        ),
    )
    if (response.isSuccess()) {
        println("Send success")
    } else {
        println("Send failure ${response.error}")
    }
}


internal suspend fun createConversationAndSendMessage(
    client: HttpClient,
    botUserId: String,
) {
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
        data = requireNotNull("hello from bot".toByteArray().base64Encode()),
    )
    println("messageRequest: $messageRequest")
    val messageResponse = client.messageService.postMessage(listOf(messageRequest))
    if (messageResponse.isSuccess()) {
        println("Bot send message success")
    } else {
        println("Bot send message failure ${messageResponse.error}")
    }
}
