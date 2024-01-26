package jvmMain.kotlin.safe

import jvmMain.kotlin.Token
import jvmMain.kotlin.botClient
import one.mixin.bot.safe.sendTransactionToUser
import java.util.UUID

fun main() {
    val traceId = UUID.randomUUID().toString()
    val transactions = sendTransactionToUser(
        botClient = botClient,
        receivers = listOf("cfb018b0-eaf7-40ec-9e07-28a5158f1269"),
        assetId = Token.CNB.assetId,
        amount = "0.00000003",
        memo = "test-memo-from-kotlin",
        traceId = traceId,
    )
    for (tx in transactions) {
        println("view transaction in: https://viewblock.io/mixin/tx/${tx.transactionHash}")
    }
}