package one.mixin.bot.blaze

import com.google.gson.Gson
import net.i2p.crypto.eddsa.EdDSAPrivateKey
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec
import okhttp3.* //ktlint-disable
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import one.mixin.bot.Constants
import one.mixin.bot.SessionToken
import one.mixin.bot.blaze.msg.Cards
import one.mixin.bot.extension.base64Decode
import one.mixin.bot.extension.base64Encode
import one.mixin.bot.signToken
import one.mixin.bot.util.base64Encode
import one.mixin.bot.util.decodeAs
import one.mixin.bot.util.encode
import one.mixin.bot.util.getRSAPrivateKeyFromString
import java.util.*
import java.util.concurrent.TimeUnit

class BlazeClient private constructor(
    private val clientToken: SessionToken,
    private val cnServer: Boolean = false,
    private val blazeHandler: BlazeHandler,
    private val parseData: Boolean = false,
    private val autoAck: Boolean = false,
    debug: Boolean = false,
) : WebSocketListener() {
    private var isConnected: Boolean = false
    private var connectNum = 0
    private val MAX_NUM = 10
    private var interval = 5000
    private val DEFAULT_INTERVAL = 5000 // 重连间隔时间，毫秒

    private var userSessionToken: SessionToken? = null
    private val ed25519 by lazy { EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519) }

    private val okHttpClient: OkHttpClient by lazy {
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
        builder.connectionPool(ConnectionPool(100, 1, TimeUnit.SECONDS))

        builder.build()
    }

    private var webSocket: WebSocket? = null

    private fun connect() {
        if (isConnected) {
            if (webSocket != null) {
                return
            }

            isConnected = false
        }

        val request = Request.Builder().addHeader("Sec-WebSocket-Protocol", "MixinBot-Blaze-1")
            .addHeader("Authorization", "Bearer " + (userSessionToken ?: clientToken).let { token ->
                signToken(
                    token.userId, token.sessionId, "GET", "/", "", if (token is SessionToken.RSA) {
                        token.privateKey
                    } else {
                        val seed = (token as SessionToken.EdDSA).seed
                        val privateSpec = EdDSAPrivateKeySpec(
                            seed.base64Decode(), ed25519
                        )
                        EdDSAPrivateKey(privateSpec)
                    }
                )
            }).url(
                if (cnServer) {
                    Constants.API.CN_WS_URL
                } else {
                    Constants.API.WS_URL
                }
            ).build()
        webSocket = okHttpClient.newWebSocket(request, this)
    }

    private fun reconnect() {
        if (connectNum <= MAX_NUM) {
            try {
                Thread.sleep(interval.toLong())
                connect()
                connectNum++
            } catch (e: Exception) {
                e.printStackTrace()
            }
            this.connectNum = 0
        } else {
            interval += 500
        }
    }

    class Builder {
        private lateinit var clientToken: SessionToken
        private var cnServer: Boolean = false
        private var debug: Boolean = false
        private var parseData: Boolean = false
        private var autoAck: Boolean = false
        private var blazeHandler: BlazeHandler = DefaultBlazeHandler()

        fun configEdDSA(
            userId: String, sessionId: String, privateKey: EdDSAPrivateKey
        ): Builder {
            clientToken = SessionToken.EdDSA(userId, sessionId, privateKey.seed.base64Encode())
            return this
        }

        fun configRSA(
            userId: String, sessionId: String, privateKey: String
        ): Builder {
            val key = getRSAPrivateKeyFromString(privateKey)
            clientToken = SessionToken.RSA(userId, sessionId, key)
            return this
        }

        fun useCNServer(): Builder {
            cnServer = true
            return this
        }

        fun enableDebug(): Builder {
            debug = true
            return this
        }

        fun enableParseData(): Builder {
            parseData = true
            return this
        }

        fun enableAutoAck(): Builder {
            autoAck = true
            return this
        }

        fun blazeHandler(blazeHandler: BlazeHandler): Builder {
            this.blazeHandler = blazeHandler
            return this
        }

        fun build(): BlazeClient {
            return BlazeClient(clientToken, cnServer, blazeHandler, parseData, autoAck, debug)
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        isConnected = false
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        isConnected = false
        reconnect()
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        try {
            // 消息通的时，重置连接次数
            connectNum = 0

            val blazeMsg = decodeAs(bytes, parseData)
            val handled = blazeHandler.onMessage(webSocket, blazeMsg)
            if (handled && autoAck) {
                if (blazeMsg.data?.messageId != null) {
                    sendAckMsg(webSocket, blazeMsg.data?.messageId!!)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        isConnected = true
        interval = DEFAULT_INTERVAL
        sendListPendingMsg(webSocket)
    }

    fun start() {
        connect()
    }
}

fun sendMsg(webSocket: WebSocket, action: Action, msgParam: MsgParam?): Boolean {
    if (msgParam?.data != null) {
        msgParam.data = base64Encode(msgParam.data!!.toByteArray())
    }

    val blazeMsg = BlazeMsg(UUID.randomUUID().toString(), action.toString(), msgParam)
    return webSocket.send(encode(blazeMsg))
}

fun sendTextMsg(
    webSocket: WebSocket, conversationId: String, recipientId: String, text: String
): Boolean {
    val msgParam =
        MsgParam(UUID.randomUUID().toString(), Category.PLAIN_TEXT.toString(), conversationId, recipientId, text)
    return sendMsg(webSocket, Action.CREATE_MESSAGE, msgParam)
}

fun sendCardMsg(
    webSocket: WebSocket, conversationId: String, recipientId: String, cards: Cards
): Boolean {
    val msgParam = MsgParam(
        UUID.randomUUID().toString(), Category.APP_CARD.toString(), conversationId, recipientId, Gson().toJson(cards)
    )
    return sendMsg(webSocket, Action.CREATE_MESSAGE, msgParam)
}


fun sendListPendingMsg(webSocket: WebSocket): Boolean {
    return sendMsg(webSocket, Action.LIST_PENDING_MESSAGES, null)
}

fun sendAckMsg(
    webSocket: WebSocket, messageId: String
): Boolean {
    val msgParam = MsgParam(messageId)
    msgParam.status = Status.READ.toString()
    return sendMsg(webSocket, Action.ACKNOWLEDGE_MESSAGE_RECEIPT, msgParam)
}
