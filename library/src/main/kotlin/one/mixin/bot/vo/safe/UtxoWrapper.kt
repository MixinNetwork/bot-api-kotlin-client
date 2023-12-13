package one.mixin.bot.vo.safe

import com.google.gson.Gson
import one.mixin.bot.vo.Utxo

data class UtxoWrapper(val outputs: List<Output>) {
    val keys: List<List<String>> by lazy { generateKeys() }
    val ids: List<String> by lazy { generateIds() }
    val lastOutput by lazy { outputs.last() }

    private val gson = Gson()

    val formatKeys: String = gson.toJson(keys)

    val input: ByteArray = gson.toJson(generateUtxos()).toByteArray()

    val firstSequence = outputs.first().sequence

    private fun generateKeys(): List<List<String>> {
        return outputs.map { it.keys }
    }

    private fun generateIds(): List<String> {
        return outputs.map { it.outputId }
    }

    private fun generateUtxos(): List<Utxo> {
        return outputs.map { output ->
            Utxo(
                hash = output.transactionHash,
                amount = output.amount,
                index = output.outputIndex,
            )
        }
    }
}
