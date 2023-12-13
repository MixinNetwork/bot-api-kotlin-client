package one.mixin.bot.api

import com.google.gson.JsonObject
import retrofit2.Call

interface ExternalService {
    fun getUtxoCall(
        hash: String,
        index: Int,
    ): Call<JsonObject>

    suspend fun getUtxo(
        hash: String,
        index: Int,
    ): JsonObject
}
