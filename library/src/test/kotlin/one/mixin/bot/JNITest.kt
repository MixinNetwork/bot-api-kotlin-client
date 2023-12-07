package one.mixin.bot

import kernel.Kernel
import one.mixin.bot.extension.base64Decode
import kotlin.test.Test
import kotlin.test.assertEquals
import java.nio.file.Path
import java.nio.file.Paths

class JNITest {
    @Test fun testLoadAndUse() {
        val currentRelativePath: Path = Paths.get("")
        val s: String = currentRelativePath.toAbsolutePath().toString()
        println("Current absolute path is: $s")

        val os = System.getProperty("os.name")
        val arch = System.getProperty("os.arch")
        println("os: $os, arch: $arch")

        System.load("${s}/libs/darwin/amd64/libgojni.so")

        val assetId = "5b9d576914e71e2362f89bb867eb69084931eb958f9a3622d776b861602275f4"
        val amount = "1"
        val address = "TV9mvdJv61mVtEoTY5h6kQtrvrULcFfadM"
        val tag = ""
        val feeAmount = ""
        val feeKeys = ""
        val feeMask = ""
        val inputs = "W3siYW1vdW50IjoiMTAiLCJoYXNoIjoiZjY0MTA2OWU0Y2NjZTYyZTcwZGU1OTI1M2MxMGY2YzUzNzRlODdjMzYxYmMzMjhiODIwNzE5ZDc5MTRmYTJlZCIsImluZGV4IjowfV0".base64Decode()
        val changeKeys = "5a21338c6e0e731afec7b87fb52447e095fb47b602bb89b0eb6ee68e8252623a"
        val changeMask = "2c911e2b2cc4f847baadddee1bb4be927a3239a436c3e28f9e736f8d436f9311"
        val extra = ""
        val tx = Kernel.buildWithdrawalTx(assetId, amount, address, tag, feeAmount, feeKeys, feeMask, inputs, changeKeys, changeMask, extra)
        println("tx: $tx")
        assertEquals(tx.hash, "a6b10f4183f7e8ab358806ad08ed686cdc3b1983dd2612ea7df8f09b52d42bb3")
    }
}