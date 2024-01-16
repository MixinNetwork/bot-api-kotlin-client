package one.mixin.bot.safe

import kernel.Kernel
import one.mixin.bot.HttpClient
import one.mixin.bot.extension.assetIdToAsset
import one.mixin.bot.extension.isUUID
import one.mixin.bot.extension.toHex
import one.mixin.bot.safe.tx.Transaction
import one.mixin.bot.util.sha3Sum256
import one.mixin.bot.util.uniqueObjectId
import one.mixin.bot.vo.GhostKeyRequest
import one.mixin.bot.vo.buildGhostKeyRequest
import one.mixin.bot.vo.safe.MixAddress
import one.mixin.bot.vo.safe.Output
import one.mixin.bot.vo.safe.SignResult
import one.mixin.bot.vo.safe.TransactionRecipient
import one.mixin.bot.vo.safe.TransactionRequest
import one.mixin.bot.vo.safe.TransactionResponse
import one.mixin.bot.vo.safe.UtxoWrapper
import java.io.IOException
import java.math.BigDecimal
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
    val asset = if (assetId.isUUID()) {
        assetIdToAsset(assetId)
    } else {
        assetId
    }

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


@Throws(SafeException::class, IOException::class)
@Deprecated("use sendTransaction instead")
fun sendTransaction(
    botClient: HttpClient,
    assetId: String,
    recipient: TransactionRecipient,
    traceId: String,
    memo: String?,
): List<TransactionResponse> { // verify trace id may have been signed already
    val txIdResp = botClient.utxoService.getTransactionsByIdCall(traceId).execute().body()
        ?: throw SafeException("get safe/transactions/{id} got null response")
    if (txIdResp.error?.code != 404) {
        throw SafeException("get safe/transactions/{id} data: ${txIdResp.data}, error: ${txIdResp.error}")
    }

    // check assetId is kernel assetId
    val asset = if (assetId.isUUID()) {
        assetIdToAsset(assetId)
    } else {
        assetId
    }

    // get unspent outputs for asset and may throw insufficient outputs error
    val (utxos, changeAmount) = requestUnspentOutputsForRecipients(botClient, assetId, recipient.amount)

    // change to the sender
    if (changeAmount > BigDecimal.ZERO) {
        val ma = MixAddress.newUuidMixAddress(listOf(botClient.safeUser.userId), 1)
            ?: throw SafeException("newUuidMixAddress got null mixAddress")
        val tr = TransactionRecipient(ma, changeAmount.toString()) // TODO
    }

    // request ghost key
    val ghostKeyReq =
        buildGhostKeyRequest(recipient.mixAddress.uuidMembers.sorted(), listOf(botClient.safeUser.userId), traceId)
    val ghostKeyResp = botClient.utxoService.ghostKeyCall(ghostKeyReq).execute().body()
    if (ghostKeyResp == null || !ghostKeyResp.isSuccess()) {
        throw SafeException("request ghostKey ${ghostKeyResp?.error}")
    }
    val ghostKeys = ghostKeyResp.data ?: throw SafeException("ghost key response data null")

    // build the unsigned raw transaction
    val utxoWrapper = UtxoWrapper(utxos)
    val receiverKeys = ghostKeys.first().keys.joinToString(",")
    val receiverMask = ghostKeys.first().mask
    val changeKeys = ghostKeys.last().keys.joinToString(",")
    val changeMask = ghostKeys.last().mask
    val tx = Kernel.buildTx(
        asset,
        recipient.amount,
        recipient.mixAddress.threshold.toInt(),
        receiverKeys,
        receiverMask,
        utxoWrapper.input,
        changeKeys,
        changeMask,
        memo,
        ""
    )
    var txResp = botClient.utxoService.transactionRequestCall(listOf(TransactionRequest(tx, traceId))).execute().body()
    if (txResp == null || !txResp.isSuccess()) {
        throw SafeException("request transaction ${txResp?.error}")
    }
    val txData = txResp.data ?: throw SafeException("request transaction response data null")

    // sign transaction
    val spendKey = botClient.safeUser.spendPrivateKey ?: throw SafeException("spend key is null")
    val views = txData.first().views.joinToString(",")
    val keys = utxoWrapper.formatKeys
    val sign = Kernel.signTx(tx, keys, views, spendKey.toHex(), false)
    val signResult = SignResult(sign.raw, sign.change)

    txResp =
        botClient.utxoService.transactionsCall(listOf(TransactionRequest(signResult.raw, traceId))).execute().body()
    if (txResp == null || !txResp.isSuccess()) {
        throw SafeException("safe/transactions ${txResp?.error}")
    }
    return txResp.data as List<TransactionResponse>
}

fun requestUnspentOutputsForRecipients(
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
