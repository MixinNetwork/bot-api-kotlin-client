package one.mixin.bot

import com.google.gson.Gson
import kernel.Kernel
import one.mixin.bot.extension.base64Decode
import one.mixin.bot.vo.safe.Output
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.measureNanoTime
import kotlin.test.Test


data class Utxo(
    val hash: String,
    val amount: String,
    val index: Int = 1,
)

data class UtxoWrapper(val outputs: List<Output>) {
    val keys: List<List<String>> by lazy { generateKeys() }
    val ids: List<String> by lazy { generateIds() }
    val lastOutput by lazy { outputs.last() }

    val formatKeys: String by lazy {
        Gson().toJson(keys)
    }

    val input: ByteArray by lazy {
        Gson().toJson(generateUtxos()).toByteArray()
    }

    val firstSequence by lazy {
        outputs.first().sequence
    }

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

class SignBenchMarkTest {
    @Test
    fun benchMark() {

    }

    @Test
    fun jniSign() {
        loadJNI()

        val utxos = listOf(
            one.mixin.bot.vo.safe.Output(
                keys = listOf(
                    "0b6c1f0d107d9b825b48d2dcd711993ad5e6b0b06501adce5db767711e97551b",
                    "e0d29cfc66eee800fe3fc2329fd49a93cb11006675fa39db6e64739a79d611ba"
                ),
                outputIndex = 0,
                transactionHash = "c513ffcc684e9585c76bd76245aa7d2def3b9f147422b59ab91db7852c9d97dd",
                mask = "",
                outputId = "",
                asset = "",
                sequence = 0,
                amount = "1",
                receivers = listOf(),
                receiversHash = "",
                receiversThreshold = 0,
                extra = "",
                state = "",
                createdAt = "",
                updatedAt = "",
                signedBy = "",
                signedAt = "",
                spentAt = "",
            )
        )

        val utxoWrapper = UtxoWrapper(utxos)

        val tx = Kernel.buildTx(
            "b9f49cf777dc4d03bc54cd1367eebca319f8603ea1ce18910d09e2c540c630d8",
            "1",
            1,
            listOf("a6c306c3137c2bf4a8bfc95ea5165f7777020916aa36ee6ec394beb9a1e6a164").joinToString(","),
            "286d4f092015ea327ba12145edd40ddede1bdd80f777c249128d38176e352a13",
            utxoWrapper.input,
            "",
            "",
            "test-memo",
            ""
        )
        println("tx: $tx")

        val priv =
            "7fb3893475a82c85e2b3c8a9a9232eddb36651ac32fa98ae83e6c2f33fb1be84dea64fa32b3b01f9a059142c0e9535a57b69f676790ae64f6d52f9a06d90f11e"


        val nanoTime = measureNanoTime {
            repeat(1000) {
                Kernel.signTx(
                    tx, utxoWrapper.formatKeys,
                    listOf(
                        "0164ba23d5aa1953132bc0bf5d12d0af7e66de2ba8773701ef135e015f24bb0b"
                    ).joinToString(","),
                    priv,
                    true,
                )
            }
        }
        println("consume: ${nanoTime.toDouble() / 1000000} ms")


    }

    private fun loadJNI() {
        val currentRelativePath: Path = Paths.get("")
        val s: String = currentRelativePath.toAbsolutePath().toString()
        println("Current absolute path is: $s")

        val os = System.getProperty("os.name")
        val arch = System.getProperty("os.arch")
        println("os: $os, arch: $arch")

        val platform = when {
            OS.isLinux() -> "linux"
            OS.isMacOSX() -> "darwin"
            OS.isWindows() -> "windows"
            else -> throw IllegalArgumentException("not supported os $os")
        }

        System.load("$s/libs/$platform/amd64/libgojni.so")
    }
}