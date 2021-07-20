package one.mixin.bot.api

import com.google.gson.JsonObject
import retrofit2.Call

interface ExternalService {
    fun getutxoCall(hash: String, index: Int): Call<JsonObject>
    suspend fun getutxo(hash: String, index: Int): JsonObject
}
