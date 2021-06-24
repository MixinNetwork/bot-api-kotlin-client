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
    implementation 'com.github.MixinNetwork:bot-api-kotlin-client:v0.4.6'
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
    <version>v0.1.0</version>
</dependency>
```

# Usage
```kotlin 
fun main() = runBlocking {
    val key = getEdDSAPrivateKeyFromString(Config.privateKey)
    val pinToken = decryASEKey(Config.pinTokenPem, key) ?: return@runBlocking
    val client = HttpClient.Builder().configEdDSA(Config.userId, Config.sessionId, key).build()

    val sessionKey = generateEd25519KeyPair()
    val publicKey = sessionKey.public as EdDSAPublicKey
    val sessionSecret = publicKey.abyte.base64Encode()

    // Create user, registering users to the Mixin network
    val user = createUser(client, sessionSecret)
    user ?: return@runBlocking
    client.setUserToken(
        SessionToken.EdDSA(
            user.userId, user.sessionId,
                (sessionKey.private as EdDSAPrivateKey).seed.base64Encode()
       )
    )
}
```
[More usage](https://github.com/MixinNetwork/bot-api-kotlin-client/blob/main/samples/src/main/java/jvmMain/kotlin/Sample.kt)

# Licence
[WTFPL](http://www.wtfpl.net/txt/copying/)
