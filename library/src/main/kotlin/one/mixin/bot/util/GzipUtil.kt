@file:Suppress("ktlint")

package one.mixin.bot.util

import okio.*
import java.io.IOException
import java.util.zip.GZIPInputStream

@Throws(IOException::class)
fun String.gzip(): ByteString {
    val result = Buffer()
    val sink = GzipSink(result).buffer()
    sink.use {
        sink.write(toByteArray())
    }
    return result.readByteString()
}

@Throws(IOException::class)
fun ByteString.ungzip(): String {
    GZIPInputStream(toByteArray().inputStream()).use { gzip ->
        return gzip.source().buffer().readUtf8()
    }
}
