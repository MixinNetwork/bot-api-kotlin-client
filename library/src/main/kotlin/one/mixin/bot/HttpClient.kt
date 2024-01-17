@file:Suppress("ktlint")

package one.mixin.bot

import com.google.gson.JsonObject
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import one.mixin.bot.Constants.API.CN_URL
import one.mixin.bot.Constants.API.URL
import one.mixin.bot.api.*
import one.mixin.bot.util.createHttpClient
import one.mixin.bot.vo.RpcRequest
import one.mixin.bot.vo.safe.SafeUser
import org.bouncycastle.jce.provider.BouncyCastleProvider
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.Security

@Suppress("unused")
class HttpClient private constructor(
    val safeUser: SafeUser,
    cnServer: Boolean = false,
    debug: Boolean = false,
    autoSwitch: Boolean = false
) {
    init {
        Security.addProvider(BouncyCastleProvider())
    }

    private val okHttpClient: OkHttpClient by lazy {
        createHttpClient(safeUser, false, debug, cnServer, autoSwitch)
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

    val tokenService: TokenService by lazy {
        retrofit.create(TokenService::class.java)
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
        private lateinit var safeUser: SafeUser
        private var cnServer: Boolean = false
        private var debug: Boolean = false
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

        fun enableAutoSwitch(): Builder {
            autoSwitch = true
            return this
        }

        fun build(): HttpClient {
            return HttpClient(safeUser, cnServer, debug, autoSwitch)
        }
    }
}
