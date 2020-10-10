package one.mixin.example

import kotlinx.coroutines.runBlocking
import one.mixin.bot.HttpClient
import one.mixin.example.Config.privateKey
import one.mixin.example.Config.sessionId
import one.mixin.example.Config.userId

fun main() = runBlocking {
    val client = HttpClient(userId, sessionId, privateKey)
    val response = client.userService.getMe()
    print(response.data?.avatarUrl)

}