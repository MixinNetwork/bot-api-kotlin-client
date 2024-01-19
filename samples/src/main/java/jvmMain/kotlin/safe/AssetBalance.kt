package jvmMain.kotlin.safe

import jvmMain.kotlin.Token
import jvmMain.kotlin.botClient
import one.mixin.bot.safe.assetBalance

fun main() {
    val balance = assetBalance(botClient, Token.CNB.assetId)
    println("balance: $balance")
}