package one.mixin.bot

import com.google.gson.JsonObject
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
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
    val safeUser: SafeUser?,
    private val accessToken: String? = null,
    debug: Boolean = false,
) {
    init {
        Security.addProvider(BouncyCastleProvider())
    }

    private val okHttpClient: OkHttpClient by lazy {
        createHttpClient(safeUser, accessToken, false, debug)
    }

    private val retrofit: Retrofit by lazy {
        val builder = Retrofit.Builder().baseUrl(URL).addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
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
        private var safeUser: SafeUser? = null
        private var debug: Boolean = false
        private var accessToken: String? = null

        fun configSafeUser(
            userId: String,
            sessionId: String,
            sessionPrivateKey: ByteArray,
            serverPublicKey: ByteArray? = null,
            spendPrivateKey: ByteArray? = null,
        ): Builder {
            safeUser =
                SafeUser(userId, sessionId, sessionPrivateKey.sliceArray(0..31), serverPublicKey, spendPrivateKey)
            return this
        }

        fun configAccessToken(accessToken: String): Builder {
            this.accessToken = accessToken
            return this
        }

        fun enableDebug(): Builder {
            debug = true
            return this
        }

        fun build(): HttpClient {
            require(!(safeUser == null && accessToken == null)) { "safeUser and accessToken can't be null at the same time" }
            return HttpClient(safeUser, accessToken, debug)
        }
    }


}
