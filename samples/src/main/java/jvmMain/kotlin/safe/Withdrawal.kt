package jvmMain.kotlin.safe

import jvmMain.kotlin.Token
import jvmMain.kotlin.botClient
import one.mixin.bot.safe.withdrawalToAddressBlocking
import java.util.UUID

fun main() {
    val traceId = UUID.randomUUID().toString()
    val transactions = withdrawalToAddressBlocking(
        botClient = botClient,
        assetId = Token.TRON_USDT.assetId,
        amount = "0.003",
        memo = "test-memo-from-kotlin",
        destination = "TAXE98CMomoA28MtNpfxUutCBq2i4bDbRv",
        tag = null,
        traceId = traceId,
    )
    for (tx in transactions) {
        println("view transaction in: https://viewblock.io/mixin/tx/${tx.transactionHash}")
    }
}
