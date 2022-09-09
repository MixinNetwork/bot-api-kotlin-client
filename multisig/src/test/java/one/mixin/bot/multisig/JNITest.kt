package one.mixin.bot.multisig

import multisig.Multisig
import java.nio.file.Path
import java.nio.file.Paths

fun main() {
    val currentRelativePath: Path = Paths.get("")
    val s: String = currentRelativePath.toAbsolutePath().toString()
    println("Current absolute path is: $s")

    System.load("${s}/multisig/libs/darwin/amd64/libgojni.so")

    val r = Multisig.buildTransaction("")
    println("buildTransaction result: $r")

    val t = Multisig.decodeTransaction("")
    println("decodeTransaction $t")
}