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
    implementation 'com.github.MixinNetwork:bot-api-kotlin-client:v0.1.0'
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
    val client = HttpClient(userId, sessionId, privateKey, true)
    val response = client.userService.getMe()
    println(response.data?.avatarUrl)

    // Create user, registering users to the Mixin network
    val sessionKey = generateRSAKeyPair()
    val sessionSecret = Base64.encodeBytes(sessionKey.public.encoded)
    val userResponse = client.userService.createUsers(
        AccountRequest(
            "User${Random().nextInt(100)}",
            sessionSecret
        )
    )
    println("${userResponse.data?.fullName} ${userResponse.data?.userId}")
    val user = userResponse.data ?: return@runBlocking
    client.setUserToken(TokenInfo(user.userId, user.sessionId, sessionKey.private))
    // Decrypt AES Key
    val userAesKey: String =
        rsaDecrypt(sessionKey.private, user.sessionId, user.pinToken)
    val pinResponse = client.userService.createPin(
        PinRequest(
            encryptPin(
                SecretPinIterator(),
                userAesKey,
                "131416"
            )!!
        )
    )
    println(pinResponse.isSuccess)

    // Get asset list
    val assetResponse = client.assetService.assets()
    println(assetResponse.data)
}
```
# Licence
[WTFPL](http://www.wtfpl.net/txt/copying/)
