package one.mixin.bot.safe.tx

import com.ionspin.kotlin.bignum.integer.BigInteger
import one.mixin.bot.extension.hexStringToByteArray
import one.mixin.bot.extension.toHex
import one.mixin.bot.util.Ed25519Util
import one.mixin.bot.util.blake3
import one.mixin.bot.util.sha512
import one.mixin.bot.util.toBytesLE
import one.mixin.bot.vo.GhostKey


private const val TX_VERSION: Byte = 0x05

private typealias SafeOutput = one.mixin.bot.vo.safe.Output

data class Transaction(
    val version: Byte = TX_VERSION, // kernel asset id
    val asset: String,
    val extra: String,
    val inputs: List<Input>,
    val outputs: List<Output>,
    val reference: List<String>,
) {
    companion object {
        fun build(
            utxos: List<SafeOutput>,
            recipients: List<TransactionRecipient>,
            ghostsKeys: List<GhostKey?>,
            extra: String,
            reference: String? = null
        ): Transaction {
            require(utxos.isNotEmpty()) { "utxos is empty" }
            require(recipients.isNotEmpty()) { "recipients is empty" }
            require(recipients.size == ghostsKeys.size) { "recipients size not match ghostsKeys size" }

            val asset = utxos[0].asset

            val inputs = mutableListOf<Input>()

            for (utxo in utxos) {
                require(utxo.asset == asset) { "utxo asset not match. ${utxo.asset} $asset" }
                inputs.add(Input(utxo.transactionHash, utxo.outputIndex))
            }

            val outputs = recipients.mapIndexed { index, recipient ->
                when (recipient) {
                    is TransactionRecipient.User -> Output(
                        OutputType.Script, recipient.amountInEthUnit(),
                        keys = ghostsKeys[index]!!.keys,
                        mask = ghostsKeys[index]!!.mask,
                        script = recipient.script,
                    )

                    is TransactionRecipient.Withdrawal -> Output(
                        OutputType.WithdrawalSubmit, recipient.amountInEthUnit(),
                        withdrawal = WithdrawalData(
                            recipient.destination,
                            recipient.tag ?: "",
                        ),
                    )

                }
            }

            return Transaction(
                asset = asset,
                extra = extra,
                inputs = inputs,
                outputs = outputs,
                reference = listOfNotNull(reference),
            )
        }

    }

    fun encodeToString(sigs: List<Map<Int, String>> = emptyList()): String {
        val encoder = Encoder()
        encoder.encodeTransaction(this)
        encoder.writeUint16(sigs.size)
        for (sig in sigs) {
            encoder.encodeSignature(sig)
        }
        return encoder.toHexString()
    }

    fun sign(views: List<String>, utxos: List<SafeOutput>, privateKey: String): String {
        val raw = encodeToString()
        val msg = raw.hexStringToByteArray().blake3()

        val spenty = privateKey.substring(0, 64).hexStringToByteArray().sha512()

        val y = Ed25519Util.setBytesWithClamping(spenty.sliceArray(0 until 32))

        val signaturesMap = mutableListOf<Map<Int, String>>()

        inputs.forEachIndexed { i, input ->

            val view = views[i]
            val utxo = utxos[i]

            require(utxo.outputIndex == input.index) { "utxo output i not match" }
            require(utxo.transactionHash == input.hash) { "utxo transaction hash not match" }

            val x = Ed25519Util.setCanonicalBytes(view.hexStringToByteArray())
            val t = Ed25519Util.scalarAdd(x, y)

            val key = t.toBytesLE()
            val public = Ed25519Util.publicKey(key)
            val index = utxo.keys.indexOf(public.toHex())
            require(index >= 0) { "public key not found in utxo keys" }

            val sig = Ed25519Util.sign(msg, key).toHex()
            val sigs = mapOf(index to sig)
            signaturesMap.add(sigs)
        }

        return encodeToString(signaturesMap)

    }

}

data class Input(
    val hash: String,
    val index: Int,
    val genesis: String? = null,
    val deposit: DepositData? = null,
    val mint: MintData? = null,
)

data class DepositData(
    val chain: String,
    val assetKey: String,
    val transaction: String,
    val index: ULong,
    val amount: BigInteger,
)

data class MintData(
    val group: String,
    val batch: ULong,
    val amount: BigInteger,
)

enum class OutputType(val value: UByte) {
    Script(0x00u), WithdrawalSubmit(0xa1u), UNKNOWN(0xffu),
}

data class Output(
    val type: OutputType,
    val amount: BigInteger,
    val keys: List<String> = listOf(),
    val withdrawal: WithdrawalData? = null,
    val script: String? = null,
    val mask: String? = null,
)

data class WithdrawalData(
    val address: String,
    val tag: String,
)