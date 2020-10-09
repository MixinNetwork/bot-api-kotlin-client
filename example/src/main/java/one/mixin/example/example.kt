package one.mixin.example

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import one.mixin.bot.HttpClient
import one.mixin.bot.util.getRSAPrivateKeyFromString

fun main() =
    runBlocking {

        val pin = "232546"
        val userId = "d066f2d2-1a91-416b-9241-f3547d99a753"
        val sessionId = "5de5ff95-8d29-4ce7-b544-817bd59f64ad"
        val privateKey =
            getRSAPrivateKeyFromString("-----BEGIN RSA PRIVATE KEY-----\r\nMIICXAIBAAKBgQCLdCKp5EI4qVOo+i6xqTf39ZLttotefFRT2wIhcbeOVswONgvb\r\ne79GlkbezC+eKbA40MkBgpMSxNCPhtMLLmvDz4l1K65B9yP5YxEatTqvtCszMA/T\r\ntW0kqDM2sIbBaPGfAH3DbUmeKGf7CGjaXb81BjBSvcXXFvz4ugW/obPdkwIDAQAB\r\nAoGANrluWOkgK4gXMnprFIDpW77c17gI3R1BIl8RaPptI8hf7zu6T3kySIr0aL4L\r\n+a82XjVgi90nxffCOHCaJQskcm+AT/ym/yg4BOU0kxWetU+n8yoniA43j3dbCVVJ\r\n3WLwv6P9Lz/BtdpbDU+b4swrcw6JkhKX3I3RSexjx7pPVPECQQDVNiaLNN7H6ics\r\nE+4j5BcrPkjUD+wH3Srk0eOQUJlqzLWKDHWDztH4VY79eOUZaZ0yYCrq58xPT7Hz\r\nIPwgPKlfAkEAp3CjFoAUGMwnztqwrUWGdF4bA5miYUIJvYrETQhwsIpg4oQlJodX\r\n+5W4XUQQ+og1H1vORlsN7b4zeBmKDLaUTQJAfZ6jBP/IhrcCD2lKtODNec//rtoW\r\nsedwP1MepcenLJKiH00J6/fuJEecsOEK8ncUhDq+7ppV+SC2cs4rXn7/NwJAA2Xj\r\nyWccAUzTWf1SLJIGooHuZmpNw0+FxxTz2uRa9Ro50R8BagDBJlfpf+sLtSniehpw\r\n2zip1bNQdBWAmbVy2QJBAIjUHzkP73n+UOb2aXoDmTORmPmgkBXpOgOmyPb3soxa\r\nhsUVVqz+H/Mz0VjhNoCB9lVNqIBYu0utVNozb6AM95Q=\r\n-----END RSA PRIVATE KEY-----\r\n")
        val client = HttpClient(userId, sessionId, privateKey)
        withContext(Dispatchers.IO){
            val response = client.userService.getMe()
            print(response.data?.avatarUrl)
        }
    }