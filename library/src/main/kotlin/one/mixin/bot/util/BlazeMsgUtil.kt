package one.mixin.bot.util

import com.google.gson.Gson
import okio.ByteString
import okio.ByteString.Companion.toByteString
import one.mixin.bot.blaze.BlazeMsg
import one.mixin.bot.extension.base64Decode

fun encode(blazeMsg: BlazeMsg): ByteString {
    val data = Gson().toJson(blazeMsg)
    return encode(data)
}

fun encode(src: String): ByteString {
    return src.gzip()
}

fun decode(src: ByteString): String {
    return src.ungzip()
}

fun decodeAs(
    src: ByteString,
    decodeData: Boolean,
): BlazeMsg {
    val data = decode(src)
    println(data)
    val blazeMsg = Gson().fromJson(data, BlazeMsg::class.java)
    if (decodeData) {
        if (blazeMsg?.data != null && blazeMsg.data?.data != null) {
            blazeMsg.data!!.data = (blazeMsg.data!!.data).base64Decode().toByteString().utf8()
        }
    }

    return blazeMsg
}
