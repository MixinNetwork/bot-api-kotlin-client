package one.mixin.bot.multisig

import multisig.Multisig
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths

class JNITest {
    @Test
    fun `test load and use`() {
        val currentRelativePath: Path = Paths.get("")
        val s: String = currentRelativePath.toAbsolutePath().toString()
        println("Current absolute path is: $s")

        val os = System.getProperty("os.name")
        val arch = System.getProperty("os.arch")
        println("os: $os, arch: $arch")

        val platform = when {
            OS.isLinux() -> "linux"
            OS.isMacOSX() -> "darwin"
            else -> throw IllegalArgumentException("not supported os $os")
        }

        System.load("${s}/libs/$platform/amd64/libgojni.so")

        val json = """
            {"version":2,"asset":"b9f49cf777dc4d03bc54cd1367eebca319f8603ea1ce18910d09e2c540c630d8","inputs":[{"hash":"8b9eac301076bd4e5e21b1613740ac9c6bb900baac1f07fd4f6d21f52f878bce","index":0}],"outputs":[{"mask":"19abf522b9eabf8cfb4c589afe216be89222cb92706c6757783394d9f550812b","keys":["b5b1da3117d94e36eef7106731816af33c6729447feb9ea99761f8c692a9d14e"],"amount":"0.01","script":"fffe01"},{"mask":"d2b190149375fa698b4e8977ccf87fc90053e239f40923437795fa8e8446ae7e","keys":["814b3b0e3782286a1ebea68f57c27cd3e1c9bd117f9e1a69e413e517cd49e1ac","713b3f689359853bf7e1294a196f26660233a4f97830a0dcf4ef306223064f32","2223e934842225fa84363d7cf3bcf01f1a63e4b4b9544068efe24bbd08400f9c"],"amount":"0.09","script":"fffe02"}],"extra":""}
        """.trimIndent()
        val r = Multisig.buildTransaction(json)
        println("buildTransaction result: $r")
        assert(r == "77770002b9f49cf777dc4d03bc54cd1367eebca319f8603ea1ce18910d09e2c540c630d800018b9eac301076bd4e5e21b1613740ac9c6bb900baac1f07fd4f6d21f52f878bce00000000000000000002000000030f42400001b5b1da3117d94e36eef7106731816af33c6729447feb9ea99761f8c692a9d14e19abf522b9eabf8cfb4c589afe216be89222cb92706c6757783394d9f550812b0003fffe010000000000038954400003814b3b0e3782286a1ebea68f57c27cd3e1c9bd117f9e1a69e413e517cd49e1ac713b3f689359853bf7e1294a196f26660233a4f97830a0dcf4ef306223064f322223e934842225fa84363d7cf3bcf01f1a63e4b4b9544068efe24bbd08400f9cd2b190149375fa698b4e8977ccf87fc90053e239f40923437795fa8e8446ae7e0003fffe02000000000000")

        val t = Multisig.decodeTransaction("")
        println("decodeTransaction $t")
    }
}