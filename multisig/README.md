# multisig

Bind Go implementation of [raw transaction in Mixin mainnet](https://github.com/MixinNetwork/multisig-bot/tree/main/common) for JVM

## Usage

```kotlin

import multisig.Multisig

fun main() {
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

    System.load("${s}/multisig/libs/$platform/amd64/libgojni.so")

    val r = Multisig.buildTransaction("")
    println("buildTransaction result: $r")

    val t = Multisig.decodeTransaction("")
    println("decodeTransaction $t")
}

```