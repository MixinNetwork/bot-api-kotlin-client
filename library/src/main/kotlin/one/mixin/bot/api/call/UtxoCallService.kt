package one.mixin.bot.api.call
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
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
interface UtxoCallService {
    @GET("safe/outputs")
    fun getOutputsCall(
        @Query("members") members: String,
        @Query("threshold") threshold: Int,
        @Query("offset") offset: Long? = null,
        @Query("limit") limit: Int = 500,
        @Query("state") state: String? = null,
    ): Call<MixinResponse<List<Output>>>

    @POST("safe/deposit/entries")
    fun createDepositCall(
        @Body depositEntryRequest: DepositEntryRequest,
    ): Call<MixinResponse<List<DepositEntry>>>

    @POST("safe/users")
    fun registerPublicKeyCall(
        @Body registerRequest: RegisterRequest,
    ): Call<MixinResponse<Account>>

    @POST("safe/keys")
    fun ghostKeyCall(
        @Body ghostKeyRequest: List<GhostKeyRequest>,
    ): Call<MixinResponse<List<GhostKey>>>

    @POST("safe/transaction/requests")
    fun transactionRequestCall(
        @Body transactionRequests: List<TransactionRequest>,
    ): Call<MixinResponse<List<TransactionResponse>>>

    @POST("safe/transactions")
    fun transactionsCall(
        @Body transactionRequests: List<TransactionRequest>,
    ): Call<MixinResponse<List<TransactionResponse>>>

    @GET("safe/transactions/{id}")
    fun getTransactionsByIdCall(
        @Path("id") id: String,
    ): Call<MixinResponse<TransactionResponse>>
}