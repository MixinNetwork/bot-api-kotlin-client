# bot-api-kotlin-client
The [Mixin Network](https://mixin.one/) based wallet allows for the rapid construction of decentralized wallets, decentralized on-chain exchanges, and other products.

# Description
This project is based on the [Mixin Network](https://mixin.one/) bot sdk of [Kotlin](https://kotlinlang.org/)

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
