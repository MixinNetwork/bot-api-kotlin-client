package one.mixin.bot.vo

import com.google.gson.Gson
import com.google.gson.JsonObject

class Utxo(
    val amount: String,
    val hash: String,
    val index: Int,
    val keys: List<String>,
    val lock: String,
    val mask: String,
    val script: String,
    val type: Int,
) {
    companion object {
        fun fromJson(json: JsonObject): Utxo {
            return Gson().fromJson(json.toString(), Utxo::class.java)
        }
    }
}
