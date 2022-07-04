# bot-api-kotlin-client
基于[Mixin Network](https://mixin.one/) 的钱包可快速构建去中心化钱包、去中心化链上交易所等产品。

# Description
本项目是基于[Kotlin](https://kotlinlang.org/)的[Mixin Network](https://mixin.one/) bot sdk


## gradle
将其添加 JitPack repository 到根build.gradle中：
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
添加库
```
dependencies {
    implementation 'com.github.MixinNetwork:bot-api-kotlin-client:v0.5.6'
}
```

## maven 
添加 JitPack repository 到 build 文件
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
添加库
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

## 关于
当用户尝试操作自己的资产时，需要 6 位 PIN 码，它的功能非常类似于私钥，丢失后无法找回资产。

```kotlin
fun encryptPin(key: String, pin: String, iterator: Long = System.currentTimeMillis() * 1_000_000): String {
    val pinByte = pin.toByteArray() + (System.currentTimeMillis() / 1000).toLeByteArray() + iterator.toLeByteArray()
    return aesEncrypt(key.base64Decode(), pinByte).base64Encode()
}
```

- 参数iterator必须是递增的并且大于 0。一般建议使用当前系统 millis 时间，也可以自己选择一个数字，每次调用递增。
- 加密后的 PIN 只能使用一次，更改密码时需要生成两次，不可重复使用。
- PIN 错误有时间锁定。如果一天失败 5 次，请不要再试，即使 5 次 PIN 正确，也会返回错误。重复更多次将导致更长的锁定时间。建议用户记下试过的 PIN 码，第二天再试。
- 一旦 PIN 丢失，就永远无法找回。建议开发者让每个用户定期输入，帮助记忆。初始设置时，请务必让用户输入 3 次以上，并提醒用户丢失无法找回
- 为了资产安全，建议提醒用户不要设置过于简单或常见的组合 PIN，如123456、111222。

# Licence
[WTFPL](http://www.wtfpl.net/txt/copying/)