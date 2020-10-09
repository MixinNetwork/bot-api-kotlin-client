package one.mixin.library.extension

import okhttp3.HttpUrl
import okhttp3.RequestBody
import okio.Buffer
import java.security.MessageDigest

fun RequestBody.bodyToString(): String {
    val buffer = Buffer()
    this.writeTo(buffer)
    return buffer.readUtf8()
}

fun HttpUrl.cutOut(): String {
    return toString().removePrefix("$scheme://$host")
}

fun String.sha256(): ByteArray {
  val md = MessageDigest.getInstance("SHA256")
  return md.digest(toByteArray())
}

fun <K, V> arrayMapOf(): HashMap<K, V> = HashMap()