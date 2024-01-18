# bot-api-kotlin-client
The [Mixin Network](https://mixin.one/) based wallet allows for the rapid construction of decentralized wallets, decentralized on-chain exchanges, and other products.

# Installation

## gradle
Add it in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add the dependency
```
dependencies {
    implementation 'com.github.MixinNetwork:bot-api-kotlin-client:v1.0.0'
}
```

## maven
Add the JitPack repository to your build file
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Add the dependency
```
<dependency>
    <groupId>com.github.MixinNetwork</groupId>
    <artifactId>bot-api-kotlin-client</artifactId>
    <version>v1.0.0</version>
</dependency>
```

## Send and Receive Messages
```kotlin
fun main(): Unit = runBlocking {
    val job = launch {
        val keyPair = newKeyPairFromPrivateKey(Config.privateKey.base64Decode())
        val blazeClient = BlazeClient.Builder()
            .configSafeUser(Config.userId, Config.sessionId, keyPair.privateKey)
            .enableDebug()
            .enableParseData()
            .enableAutoAck()
            .blazeHandler(MyBlazeHandler())
            .build()
        blazeClient.start()
    }
    job.join()
}

private class MyBlazeHandler : BlazeHandler {
    override fun onMessage(webSocket: WebSocket, blazeMsg: BlazeMsg): Boolean {
        println(blazeMsg)
        blazeMsg.data?.let { data ->
            sendTextMsg(webSocket, data.conversionId, data.userId, "read")
        }
        return true
    }
}
```

# Licence
[WTFPL](http://www.wtfpl.net/txt/copying/)
