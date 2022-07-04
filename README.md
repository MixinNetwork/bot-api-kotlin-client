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
    implementation 'com.github.MixinNetwork:bot-api-kotlin-client:v0.5.6'
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

## About PIN 
A 6-digit PIN is required when a user is trying to transfer assets, the code functions pretty much like a private key, not retrievable if lost.

```kotlin
fun encryptPin(key: String, pin: String, iterator: Long = System.currentTimeMillis() * 1_000_000): String {
    val pinByte = pin.toByteArray() + (System.currentTimeMillis() / 1000).toLeByteArray() + iterator.toLeByteArray()
    return aesEncrypt(key.base64Decode(), pinByte).base64Encode()
}
```

- The parameter iterator must be incremental and greater than 0. It is generally recommended to use the current system millis time, or you can choose a number by yourself, and increment it with each call.
- The encrypted PIN can only be used once, and it needs to be generated twice when changing the password and cannot be reused.
- There is a time lock for PIN errors. If you have failed 5 times a day, do not try again, even the PIN is correct after 5 times, an error will be returned. Repeating more times will cause a longer lock time. It is recommended that users write down the tried PIN and try again the next day.
- Once a PIN is lost, it can never be retrieved. It is recommended that the developer let each user enter it regularly to help memorize it. During the initial setting, make sure to let the user enter it more than 3 times and remind the user that it cannot be retrieved if lost
- For asset security, it is recommended to remind users not to set PINs that are too simple or common combinations, such as 123456, 111222.

# Licence
[WTFPL](http://www.wtfpl.net/txt/copying/)
