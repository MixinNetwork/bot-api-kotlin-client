package one.mixin.bot.util

import net.i2p.crypto.eddsa.EdDSAPrivateKey
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import one.mixin.bot.Constants
import one.mixin.bot.SessionToken
import one.mixin.bot.api.exception.ClientErrorException
import one.mixin.bot.api.exception.ServerErrorException
import one.mixin.bot.extension.HostSelectionInterceptor
import one.mixin.bot.extension.base64Decode
import one.mixin.bot.extension.isNeedSwitch
import one.mixin.bot.signToken
import java.util.*
import java.util.concurrent.TimeUnit

fun createHttpClient(userSessionToken: SessionToken?, clientToken: SessionToken, websocket: Boolean, debug: Boolean, cnServer: Boolean, autoSwitch: Boolean): OkHttpClient {
    val builder = OkHttpClient.Builder()
    if (debug) {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        builder.addNetworkInterceptor(logging)
    }
    builder.connectTimeout(10, TimeUnit.SECONDS)
    builder.writeTimeout(10, TimeUnit.SECONDS)
    builder.readTimeout(10, TimeUnit.SECONDS)
    builder.pingInterval(15, TimeUnit.SECONDS)
    builder.retryOnConnectionFailure(false)

    if (websocket) {
        builder.addInterceptor(
            HostSelectionInterceptor.get(
                if (cnServer) {
                    Constants.API.CN_WS_URL
                } else {
                    Constants.API.WS_URL
                }
            )
        )
    } else {
        builder.addInterceptor(
            HostSelectionInterceptor.get(
                if (cnServer) {
                    Constants.API.CN_URL
                } else {
                    Constants.API.URL
                }
            )
        )
    }

    builder.addInterceptor(
        Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()

            if (websocket) {
                requestBuilder.addHeader("Sec-WebSocket-Protocol", "MixinBot-Blaze-1")
            }
            requestBuilder.addHeader("User-Agent", Constants.UA).addHeader("Accept-Language", Locale.getDefault().language).addHeader(
                "Authorization",
                "Bearer " + (
                    userSessionToken
                        ?: clientToken
                    ).let { token ->
                    signToken(
                        token.userId, token.sessionId, chain.request(),
                        if (token is SessionToken.RSA) {
                            token.privateKey
                        } else {
                            val seed = (token as SessionToken.EdDSA).seed
                            val privateSpec = EdDSAPrivateKeySpec(seed.base64Decode(), ed25519)
                            EdDSAPrivateKey(privateSpec)
                        }
                    )
                }
            )

            val request = requestBuilder.build()

            val response = try {
                chain.proceed(request)
            } catch (e: Exception) {
                throw e.apply {
                    if (autoSwitch && e.isNeedSwitch()) {
                        HostSelectionInterceptor.get().switch(request)
                    } else {
                        if (e.message?.contains("502") == true) {
                            throw ServerErrorException(502)
                        } else throw e
                    }
                }
            }

            if (!response.isSuccessful) {
                val code = response.code
                if (code in 500..599) {
                    throw ServerErrorException(code)
                } else if (code in 400..499) {
                    throw ClientErrorException(code)
                }
            }
            response
        }
    )

    return builder.build()
}
