# bot-api-kotlin-client
基于[Mixin Network](https://mixin.one/) 的钱包可快速构建去中心化钱包、去中心化链上交易所等产品。

# Description
本项目是基于[Kotlin](https://kotlinlang.org/)的[Mixin Network](https://mixin.one/) bot sdk

# Usage
```kotlin 
fun main() = runBlocking {
    val client = HttpClient(userId, sessionId, privateKey, true)
    val response = client.userService.getMe()
    println(response.data?.avatarUrl)

    // create user 将用户注册到 Mixin 网络
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
    // decrypt pin token
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

    val assetResponse = client.assetService.assets()
    println(assetResponse.data)
}
```
# Licence
[WTFPL](http://www.wtfpl.net/txt/copying/)