package jvmMain.kotlin.legacy

import jvmMain.kotlin.Config
import jvmMain.kotlin.Token
import jvmMain.kotlin.botClient
import kotlinx.coroutines.runBlocking
import one.mixin.bot.encryptTipPin
import one.mixin.bot.extension.hexStringToByteArray
import one.mixin.bot.safe.TipBody
import one.mixin.bot.util.decryptPinToken
import one.mixin.bot.util.publicKeyToCurve25519
import one.mixin.bot.vo.TransferRequest
import java.util.UUID


fun main(): Unit = runBlocking {
    val pinToken = publicKeyToCurve25519(Config.BOT_SERVER_PUBLIC_KEY.hexStringToByteArray())
    val aesKey = decryptPinToken(
        pinToken,
        Config.BOT_SESSION_PRIVATE_KEY.hexStringToByteArray(),
    )

    val assetId = Token.CNB.assetId
    val amount = "0.0000001"
    val userId = "cfb018b0-eaf7-40ec-9e07-28a5158f1269"
    val traceId = UUID.randomUUID().toString()
    val memo = "test"

    val signTarget = TipBody.forTransfer(
        assetId = assetId,
        amount = amount,
        counterUserId = userId,
        traceId = traceId,
        memo = memo,
    )
    val pin = encryptTipPin(
        aesKey,
        signTarget,
        Config.BOT_SPEND_KEY.hexStringToByteArray(),
    )
    botClient.snapshotService.transfer(
        TransferRequest(
            assetId = assetId,
            amount = amount,
            opponentId = userId,
            traceId = traceId,
            memo = memo,
            pin = pin,
        )
    )
}