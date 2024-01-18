package one.mixin.bot.safe

import kotlinx.coroutines.runBlocking
import one.mixin.bot.Constants
import one.mixin.bot.HttpClient
import one.mixin.bot.api.MixinResponse
import one.mixin.bot.extension.hexStringToByteArray
import one.mixin.bot.extension.toHex
import one.mixin.bot.safe.tx.Transaction
import one.mixin.bot.util.blake3
import one.mixin.bot.util.sha3Sum256
import one.mixin.bot.util.uniqueObjectId
import one.mixin.bot.vo.GhostKeyRequest
import one.mixin.bot.vo.safe.Output
import one.mixin.bot.vo.safe.TransactionRequest
import one.mixin.bot.vo.safe.TransactionResponse
import java.io.IOException
import java.math.BigDecimal
import java.util.UUID
import one.mixin.bot.safe.tx.TransactionRecipient as TxRecipient


private fun verifyTxId(botClient: HttpClient, traceId: String) {
    val txIdResp = botClient.utxoService.getTransactionsByIdCall(traceId).execute().body()
        ?: throw SafeException("get safe/transactions/{id} got null response")
    if (txIdResp.error?.code != 404) {
        throw SafeException("get safe/transactions/{id} data: ${txIdResp.data}, error: ${txIdResp.error}")
    }
}

fun sendTransactionToUser(
    botClient: HttpClient,
    assetId: String,
    receivers: List<String>,
    amount: String,
    memo: String?,
    traceId: String,
): List<TransactionResponse> {
    verifyTxId(botClient, traceId) // check assetId is kernel assetId

    // get unspent outputs for asset and may throw insufficient outputs error
    val (utxos, changeAmount) = requestUnspentOutputsForRecipients(botClient, assetId, amount)

    val recipients = buildList<TxRecipient> {
        add(TxRecipient.User(receivers, amount, 1))
        if (changeAmount > BigDecimal.ZERO) {
            add(
                TxRecipient.User(
                    utxos.first().receivers, changeAmount.toString(), utxos.first().receiversThreshold
                )
            )
        }

    }

    val ghostKeyRequest = buildList {
        val output = uniqueObjectId(traceId, "OUTPUT", "0")
        add(GhostKeyRequest(receivers, 0, output))
        if (changeAmount > BigDecimal.ZERO) {
            val change = uniqueObjectId(traceId, "OUTPUT", "1")
            add(GhostKeyRequest(utxos.first().receivers, 1, change))
        }
    }
    val ghostKeyResponse = botClient.utxoService.ghostKeyCall(ghostKeyRequest).execute().body()

    if (ghostKeyResponse == null || !ghostKeyResponse.isSuccess()) {
        throw SafeException("request ghostKey ${ghostKeyResponse?.error}")
    }
    val ghostKeys = ghostKeyResponse.data ?: throw SafeException("ghost key response data null")

    val tx = Transaction.build(
        utxos = utxos,
        recipients = recipients,
        ghostsKeys = ghostKeys,
        extra = memo ?: "",
    )

    val verifiedResp = botClient.utxoService.transactionRequestCall(
        listOf(TransactionRequest(tx.encodeToString(), traceId))
    ).execute().body()
    if (verifiedResp == null || !verifiedResp.isSuccess()) {
        throw SafeException("request transaction ${verifiedResp?.error}")
    }
    val verifiedTx = verifiedResp.data?.firstOrNull() ?: throw SafeException("request transaction response data null")

    val spendKey = botClient.safeUser.spendPrivateKey ?: throw SafeException("spend key is null")
    val signedRaw = tx.sign(verifiedTx.views, utxos, spendKey.toHex())

    val sendTx = botClient.utxoService.transactionsCall(
        listOf(TransactionRequest(signedRaw, traceId))
    ).execute().body()

    if (sendTx == null || !sendTx.isSuccess()) {
        throw SafeException("safe/transactions ${sendTx?.error}")
    }
    return sendTx.data!!
}


@JvmName("withdrawalToAddress")
@Throws(SafeException::class, IOException::class, UtxoException::class)
fun withdrawalToAddressBlocking(
    botClient: HttpClient,
    assetId: String,
    destination: String,
    tag: String?,
    amount: String,
    memo: String? = null,
    traceId: String = UUID.randomUUID().toString(),
): List<TransactionResponse> = runBlocking {
    withdrawalToAddress(botClient, assetId, destination, tag, amount, memo, traceId)
}

@JvmSynthetic
@Throws(SafeException::class, IOException::class, UtxoException::class)
suspend fun withdrawalToAddress(
    botClient: HttpClient,
    assetId: String,
    destination: String,
    tag: String?,
    amount: String,
    memo: String? = null,
    traceId: String = UUID.randomUUID().toString(),
): List<TransactionResponse> {
    verifyTxId(botClient, traceId)
    val token = botClient.tokenService.getAssetById(assetId).requiredData()
    val chain = if (token.assetId == token.chainId) {
        token
    } else {
        botClient.tokenService.getAssetById(token.chainId).requiredData()
    }

    val fee = botClient.tokenService.getFees(assetId, destination).requiredData().first {
        it.assetId == chain.assetId
    }

    return botClient.withdrawalTransaction(
        feeReceiverId = Constants.MIXIN_FEE_USER_ID,
        feeAssetId = chain.assetId,
        feeAmount = fee.amount!!.toBigDecimal(),
        assetId = token.assetId,
        amount = amount.toBigDecimal(),
        destination = destination,
        tag = tag,
        memo = memo,
        traceId = traceId,
    )

}


private suspend fun HttpClient.withdrawalTransaction(
    feeReceiverId: String,
    feeAssetId: String,
    feeAmount: BigDecimal,
    assetId: String,
    amount: BigDecimal,
    destination: String,
    tag: String?,
    memo: String?,
    traceId: String,
): List<TransactionResponse> = if (feeAssetId != assetId) {
    val (utxos, change) = requestUnspentOutputsForRecipients(this, assetId, amount.toPlainString())
    val (feeUtxos, feeChange) = requestUnspentOutputsForRecipients(this, feeAssetId, feeAmount.toPlainString())

    val feeTraceId = uniqueObjectId(traceId, "FEE")

    val ghosts = utxoService.ghostKey(buildList { // fee
        add(GhostKeyRequest(listOf(feeReceiverId), 0, uniqueObjectId(feeTraceId, "OUTPUT", "0")))

        // change
        if (change > BigDecimal.ZERO) {
            add(GhostKeyRequest(utxos.first().receivers, 1, uniqueObjectId(traceId, "OUTPUT", "1")))
        }

        // fee change
        if (feeChange > BigDecimal.ZERO) {
            add(GhostKeyRequest(feeUtxos.first().receivers, 1, uniqueObjectId(feeTraceId, "OUTPUT", "1")))
        }
    }).requiredData()

    val feeGhostKey = ghosts.first()
    val changeGhostKey = if (change > BigDecimal.ZERO) ghosts[1] else null
    val feeChangeGhostKey = if (feeChange > BigDecimal.ZERO) ghosts.last() else null

    val withdrawalTx = Transaction.build(
        utxos = utxos,
        recipients = buildList {
            add(TxRecipient.Withdrawal(destination = destination, tag = tag, amount = amount.toPlainString()))
            if (change > BigDecimal.ZERO) {
                add(
                    TxRecipient.User(
                        members = utxos.first().receivers,
                        amount = change.toPlainString(),
                        threshold = utxos.first().receiversThreshold,
                    )
                )
            }

        },
        ghostsKeys = buildList {
            add(null) // first is for withdrawal
            if (change > BigDecimal.ZERO) {
                add(changeGhostKey)
            }
        },
        extra = memo ?: "",
    )
    val feeTx = Transaction.build(
        utxos = feeUtxos,
        recipients = buildList {
            add(TxRecipient.User(listOf(feeReceiverId), feeAmount.toPlainString(), 1))
            if (feeChange > BigDecimal.ZERO) {
                add(
                    TxRecipient.User(
                        members = feeUtxos.first().receivers,
                        amount = feeChange.toPlainString(),
                        threshold = feeUtxos.first().receiversThreshold,
                    )
                )
            }
        },
        ghostsKeys = buildList {
            add(feeGhostKey)
            if (feeChange > BigDecimal.ZERO) {
                add(feeChangeGhostKey)
            }
        },
        extra = memo ?: "",
        reference = withdrawalTx.encodeToString().hexStringToByteArray().blake3().toHex(),
    )

    val requestResponse = utxoService.transactionRequest(
        listOf(
            TransactionRequest(withdrawalTx.encodeToString(), traceId),
            TransactionRequest(feeTx.encodeToString(), feeTraceId),
        )
    ).requiredData()
    if (requestResponse.isEmpty()) {
        throw SafeException("request transaction response data null")
    } else if (requestResponse.first().state != "unspent") {
        throw SafeException("request transaction state not unspent")
    }

    val withdrawalData = requestResponse.first { it.requestId == traceId }
    val feeData = requestResponse.first { it.requestId == feeTraceId }

    val spendKey = safeUser.spendPrivateKey ?: throw SafeException("spend key is null")

    val signedWithdrawalRaw = withdrawalTx.sign(withdrawalData.views, utxos, spendKey.toHex())
    val signedFeeRaw = feeTx.sign(feeData.views, feeUtxos, spendKey.toHex())

    utxoService.transactions(
        listOf(
            TransactionRequest(signedWithdrawalRaw, traceId),
            TransactionRequest(signedFeeRaw, feeTraceId),
        )
    ).requiredData()

} else {
    val (utxos, changeAmount) = requestUnspentOutputsForRecipients(this, assetId, (amount + feeAmount).toPlainString())

    val ghostKeys = utxoService.ghostKey(buildList { // fee

        // fee
        val output = uniqueObjectId(traceId, "OUTPUT", "1")
        add(GhostKeyRequest(listOf(feeReceiverId), 1, output))

        // change
        if (changeAmount > BigDecimal.ZERO) {
            val change = uniqueObjectId(traceId, "OUTPUT", "2")
            add(GhostKeyRequest(utxos.first().receivers, 2, change))
        }
    }).requiredData()

    val tx = Transaction.build(
        utxos = utxos,
        recipients = buildList {

            // withdrawal
            add(TxRecipient.Withdrawal(destination = destination, tag = tag, amount = amount.toPlainString()))

            // fee
            add(TxRecipient.User(members = listOf(feeReceiverId), amount = feeAmount.toPlainString(), threshold = 1))

            // change
            if (changeAmount > BigDecimal.ZERO) {
                add(
                    TxRecipient.User(
                        members = utxos.first().receivers,
                        amount = changeAmount.toPlainString(),
                        threshold = utxos.first().receiversThreshold
                    )
                )
            }
        },
        ghostsKeys = listOf(null) /* first is for withdrawal */ + ghostKeys,
        extra = memo ?: "",
    )

    val verifiedTx =
        utxoService.transactionRequest(listOf(TransactionRequest(tx.encodeToString(), traceId))).requiredData()
            .firstOrNull() ?: throw SafeException("request transaction response data null")

    val spendKey = safeUser.spendPrivateKey ?: throw SafeException("spend key is null")
    val signedRaw = tx.sign(verifiedTx.views, utxos, spendKey.toHex())

    utxoService.transactions(listOf(TransactionRequest(signedRaw, traceId))).requiredData()
}

private fun requestUnspentOutputsForRecipients(
    botClient: HttpClient,
    assetId: String,
    amount: String,
): Pair<List<Output>, BigDecimal> {
    val memberHash = buildHashMembers(listOf(botClient.safeUser.userId))
    val outputs = listUnspentOutputs(botClient, memberHash, 1, assetId)
    if (outputs.isEmpty()) {
        throw UtxoException(BigDecimal.ZERO, BigDecimal.ZERO, 0)
    }
    val totalOutput = BigDecimal(amount)
    val selectedOutputs = mutableListOf<Output>()
    var totalInput = BigDecimal.ZERO
    outputs.forEach { o ->
        val a = BigDecimal(o.amount)
        totalInput = totalInput.add(a)
        selectedOutputs.add(o)
        if (totalInput >= totalOutput) {
            return Pair(selectedOutputs, totalInput - totalOutput)
        }
    }
    throw UtxoException(totalInput, totalOutput, outputs.size)
}

fun buildHashMembers(ids: List<String>): String {
    return ids.sortedBy { it }.joinToString("").sha3Sum256().joinToString("") { "%02x".format(it) }
}

private fun <T> MixinResponse<T>.requiredData(): T {
    if (isSuccess()) {
        return data!!
    }
    throw SafeException("response error: $error")
}
