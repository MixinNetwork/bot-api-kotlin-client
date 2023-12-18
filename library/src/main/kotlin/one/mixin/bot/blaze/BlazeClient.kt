@file:Suppress("ktlint", "unused")

package one.mixin.bot.blaze

import com.google.gson.Gson
import okhttp3.*
import okio.ByteString
import one.mixin.bot.Constants
import one.mixin.bot.blaze.msg.Buttons
import one.mixin.bot.blaze.msg.Cards
import one.mixin.bot.extension.base64Encode
import one.mixin.bot.util.*
import one.mixin.bot.vo.safe.SafeUser
import java.util.*

class BlazeClient private constructor(
    private val safeUser: SafeUser,
    private val cnServer: Boolean = false,
    private val blazeHandler: BlazeHandler,
    private val parseData: Boolean = false,
    private val autoAck: Boolean = false,
    debug: Boolean = false,
    autoSwitch: Boolean = false,
) : WebSocketListener() {
    companion object {
        private const val MAX_RECONNECT_COUNT = 10
        private const val DEFAULT_RECONNECT_INTERVAL = 5000
    }

    private var isConnected: Boolean = false
    private var connectCount = 0
    private var reconnectInterval = 5000

    private val okHttpClient: OkHttpClient by lazy {
        createHttpClient(safeUser, true, debug, cnServer, autoSwitch)
    }

    private var webSocket: WebSocket? = null

    private fun connect() {
        if (isConnected) {
            if (webSocket != null) {
                return
            }

            isConnected = false
        }

        val request = Request.Builder().url(
            if (cnServer) {
                Constants.API.CN_WS_URL
            } else {
                Constants.API.WS_URL
            }
        ).build()
        webSocket = okHttpClient.newWebSocket(request, this)
    }

    private fun reconnect() {
        if (connectCount <= MAX_RECONNECT_COUNT) {
            try {
                Thread.sleep(reconnectInterval.toLong())
                connect()
                connectCount++
            } catch (e: Exception) {
                e.printStackTrace()
            }
            this.connectCount = 0
        } else {
            reconnectInterval += 500
        }
    }

    class Builder {
        private lateinit var safeUser: SafeUser
        private var cnServer: Boolean = false
        private var debug: Boolean = false
        private var parseData: Boolean = false
        private var autoAck: Boolean = false
        private var blazeHandler: BlazeHandler = DefaultBlazeHandler()
        private var autoSwitch: Boolean = false

        fun configSafeUser(
            userId: String,
            sessionId: String,
            sessionPrivateKey: ByteArray,
            serverPublicKey: ByteArray? = null,
            spendPrivateKey: ByteArray? = null,
        ): Builder {
            safeUser = SafeUser(userId, sessionId, sessionPrivateKey.sliceArray(0..31), serverPublicKey, spendPrivateKey)
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

        fun enableAutoSwitch(): Builder {
            autoSwitch = true
            return this
        }

        fun build(): BlazeClient {
            return BlazeClient(safeUser, cnServer, blazeHandler, parseData, autoAck, autoSwitch)
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
            connectCount = 0

            val blazeMsg = decodeAs(bytes, parseData)
            val handled = blazeHandler.onMessage(webSocket, blazeMsg)
            if (handled && autoAck) {
                if (blazeMsg.data?.messageId != null) {
                    sendAckMsg(webSocket, blazeMsg.data?.messageId!!)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        isConnected = true
        reconnectInterval = DEFAULT_RECONNECT_INTERVAL
        sendListPendingMsg(webSocket)
    }

    fun start() {
        connect()
    }
}

fun sendMsg(webSocket: WebSocket, action: Action, msgParam: MsgParam?): Boolean {
    if (action == Action.CREATE_MESSAGE && msgParam?.conversionId.isNullOrEmpty()) {
        return false
    }
    if (msgParam?.data != null) {
        msgParam.data = msgParam.data!!.toByteArray().base64Encode()
    }
    val blazeMsg = BlazeMsg(UUID.randomUUID().toString(), action.toString(), msgParam)
    return webSocket.send(encode(blazeMsg))
}

fun sendTextMsg(webSocket: WebSocket, conversationId: String, recipientId: String, text: String): Boolean {
    val msgParam = MsgParam(UUID.randomUUID().toString(), Category.PLAIN_TEXT.toString(), conversationId, recipientId, text)
    return sendMsg(webSocket, Action.CREATE_MESSAGE, msgParam)
}

fun sendCardMsg(webSocket: WebSocket, conversationId: String, recipientId: String, cards: Cards): Boolean {
    val msgParam = MsgParam(UUID.randomUUID().toString(), Category.APP_CARD.toString(), conversationId, recipientId, Gson().toJson(cards))
    return sendMsg(webSocket, Action.CREATE_MESSAGE, msgParam)
}

fun sendButtonsMsg(webSocket: WebSocket, conversationId: String, recipientId: String, buttons: List<Buttons>): Boolean {
    val msgParam = MsgParam(UUID.randomUUID().toString(), Category.APP_BUTTON_GROUP.toString(), conversationId, recipientId, Gson().toJson(buttons))
    return sendMsg(webSocket, Action.CREATE_MESSAGE, msgParam)
}

fun sendListPendingMsg(webSocket: WebSocket): Boolean {
    return sendMsg(webSocket, Action.LIST_PENDING_MESSAGES, null)
}

fun sendAckMsg(webSocket: WebSocket, messageId: String): Boolean {
    val msgParam = MsgParam(messageId)
    msgParam.status = Status.READ.toString()
    return sendMsg(webSocket, Action.ACKNOWLEDGE_MESSAGE_RECEIPT, msgParam)
}
