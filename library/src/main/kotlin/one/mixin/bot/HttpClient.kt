package one.mixin.bot

import com.google.gson.JsonObject
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import java.security.Security
import okhttp3.OkHttpClient
import one.mixin.bot.Constants.API.CN_URL
import one.mixin.bot.Constants.API.URL
import one.mixin.bot.api.* //ktlint-disable
import one.mixin.bot.tip.EdKeyPair
import one.mixin.bot.util.createHttpClient
import one.mixin.bot.util.getRSAPrivateKeyFromString
import one.mixin.bot.vo.RpcRequest
import org.bouncycastle.jce.provider.BouncyCastleProvider
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("unused")
class HttpClient private constructor(
    private val clientToken: SessionToken,
    cnServer: Boolean = false,
    debug: Boolean = false,
    autoSwitch: Boolean = false
) {
    init {
        Security.addProvider(BouncyCastleProvider())
    }

    private val okHttpClient: OkHttpClient by lazy {
        createHttpClient(clientToken, false, debug, cnServer, autoSwitch)
    }

    private val retrofit: Retrofit by lazy {
        val builder = Retrofit.Builder()
            .baseUrl(
                if (cnServer) {
                    CN_URL
                } else {
                    URL
                }
            )
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
        builder.build()
    }

    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val assetService: AssetService by lazy {
        retrofit.create(AssetService::class.java)
    }

    val addressService: AddressService by lazy {
        retrofit.create(AddressService::class.java)
    }

    val snapshotService: SnapshotService by lazy {
        retrofit.create(SnapshotService::class.java)
    }

    val messageService: MessageService by lazy {
        retrofit.create(MessageService::class.java)
    }

    val conversationService: ConversationService by lazy {
        retrofit.create(ConversationService::class.java)
    }

    val attachmentService: AttachmentService by lazy {
        retrofit.create(AttachmentService::class.java)
    }

    val utxoService: UtxoService by lazy {
        retrofit.create(UtxoService::class.java)
    }

    val externalService: ExternalService by lazy {
        object : ExternalService {
            override fun getUtxoCall(hash: String, index: Int): Call<JsonObject> {
                val list = mutableListOf<Any>()
                list.add(hash)
                list.add(index)
                return userService.mixinMainnetRPCCall(RpcRequest("getutxo", list))
            }

            override suspend fun getUtxo(hash: String, index: Int): JsonObject {
                val list = mutableListOf<Any>()
                list.add(hash)
                list.add(index)
                return userService.mixinMainnetRPC(RpcRequest("getutxo", list))
            }
        }
    }

    class Builder {
        private lateinit var clientToken: SessionToken
        private var cnServer: Boolean = false
        private var debug: Boolean = false
        private var autoSwitch: Boolean = false

        fun configEdDSA(
            userId: String,
            sessionId: String,
            keyPair: EdKeyPair,
        ): Builder {
            clientToken = SessionToken.EdDSA(userId, sessionId, keyPair)
            return this
        }

        fun configRSA(
            userId: String,
            sessionId: String,
            privateKey: String
        ): Builder {
            val key = getRSAPrivateKeyFromString(privateKey)
            clientToken =
                SessionToken.RSA(userId, sessionId, key)
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

        fun enableAutoSwitch(): Builder {
            autoSwitch = true
            return this
        }

        fun build(): HttpClient {
            return HttpClient(clientToken, cnServer, debug, autoSwitch)
        }
    }
}
