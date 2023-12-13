package one.mixin.bot.safe

import one.mixin.bot.HttpClient
import one.mixin.bot.vo.safe.Output

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
    val resp = botClient.utxoService.getOutputsCall(membersHash, threshold, offset, limit, state, assetId).execute().body()
    if (resp == null || !resp.isSuccess()) {
        throw SafeException("get safe/outputs ${resp?.error}")
    }
    return resp.data as List<Output>
}
