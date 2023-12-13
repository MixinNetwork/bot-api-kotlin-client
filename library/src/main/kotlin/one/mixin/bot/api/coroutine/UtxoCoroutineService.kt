package one.mixin.bot.api.coroutine
import one.mixin.bot.api.MixinResponse
import one.mixin.bot.vo.Account
import one.mixin.bot.vo.DepositEntry
import one.mixin.bot.vo.GhostKey
import one.mixin.bot.vo.GhostKeyRequest
import one.mixin.bot.vo.RegisterRequest
import one.mixin.bot.vo.safe.DepositEntryRequest
import one.mixin.bot.vo.safe.Output
import one.mixin.bot.vo.safe.TransactionRequest
import one.mixin.bot.vo.safe.TransactionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UtxoCoroutineService {
    @GET("safe/outputs")
    suspend fun getOutputs(
        @Query("members") members: String,
        @Query("threshold") threshold: Int,
        @Query("offset") offset: Long? = null,
        @Query("limit") limit: Int = 500,
        @Query("state") state: String? = null,
        @Query("asset") asset: String? = null,
    ): MixinResponse<List<Output>>

    @POST("safe/deposit/entries")
    suspend fun createDeposit(
        @Body depositEntryRequest: DepositEntryRequest,
    ): MixinResponse<List<DepositEntry>>

    @POST("safe/users")
    suspend fun registerPublicKey(
        @Body registerRequest: RegisterRequest,
    ): MixinResponse<Account>

    @POST("safe/keys")
    suspend fun ghostKey(
        @Body ghostKeyRequest: List<GhostKeyRequest>,
    ): MixinResponse<List<GhostKey>>

    @POST("safe/transaction/requests")
    suspend fun transactionRequest(
        @Body transactionRequests: List<TransactionRequest>,
    ): MixinResponse<List<TransactionResponse>>

    @POST("safe/transactions")
    suspend fun transactions(
        @Body transactionRequests: List<TransactionRequest>,
    ): MixinResponse<List<TransactionResponse>>

    @GET("safe/transactions/{id}")
    suspend fun getTransactionsById(
        @Path("id") id: String,
    ): MixinResponse<TransactionResponse>
}
