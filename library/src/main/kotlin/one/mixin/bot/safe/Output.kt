package one.mixin.bot.safe

import one.mixin.bot.HttpClient
import one.mixin.bot.vo.safe.Output
import java.math.BigDecimal


fun assetBalance(
    botClient: HttpClient, assetId: String,
    members: List<String> = listOf(botClient.safeUser.userId),
): String {
    val outputs = listUnspentOutputs(
        botClient, buildHashMembers(members), 1, assetId
    )
    var amount = BigDecimal.ZERO
    for (output in outputs) {
        amount += BigDecimal(output.amount)
    }
    return amount.toPlainString()
}

fun listUnspentOutputs(
    botClient: HttpClient,
    membersHash: String,
    threshold: Int,
    kernelAssetId: String,
): List<Output> {
    return listOutputs(botClient, membersHash, threshold, kernelAssetId, "unspent", 0, 500)
}

fun listOutputs(
    botClient: HttpClient,
    membersHash: String,
    threshold: Int,
    assetId: String,
    state: String,
    offset: Long,
    limit: Int,
): List<Output> {
    val resp =
        botClient.utxoService.getOutputsCall(membersHash, threshold, offset, limit, state, assetId).execute().body()
    if (resp == null || !resp.isSuccess()) {
        throw SafeException("get safe/outputs ${resp?.error}")
    }
    return resp.data as List<Output>
}
