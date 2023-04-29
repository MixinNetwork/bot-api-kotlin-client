package one.mixin.bot.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

fun compress(uncompressedData: ByteArray): ByteArray {
    var result = ByteArray(0)
    try {
        val bos = ByteArrayOutputStream(uncompressedData.size)
        var var3: Throwable? = null
        try {
            val gzipOS = GZIPOutputStream(bos)
            var var5: Throwable? = null
            try {
                gzipOS.write(uncompressedData)
                gzipOS.close()
                result = bos.toByteArray()
            } catch (var30: Throwable) {
                var5 = var30
                throw var30
            } finally {
                if (var5 != null) {
                    try {
                        gzipOS.close()
                    } catch (var29: Throwable) {
                        var5.addSuppressed(var29)
                    }
                } else {
                    gzipOS.close()
                }
            }
        } catch (var32: Throwable) {
            var3 = var32
            throw var32
        } finally {
            if (var3 != null) {
                try {
                    bos.close()
                } catch (var28: Throwable) {
                    var3.addSuppressed(var28)
                }
            } else {
                bos.close()
            }
        }
    } catch (var34: IOException) {
        var34.printStackTrace()
    }
    return result
}

fun decompress(compressedData: ByteArray?): ByteArray {
    var result = ByteArray(0)
    try {
        val bis = ByteArrayInputStream(compressedData)
        var var3: Throwable? = null
        try {
            val bos = ByteArrayOutputStream()
            var var5: Throwable? = null
            try {
                val gzipIS = GZIPInputStream(bis)
                var var7: Throwable? = null
                try {
                    val buffer = ByteArray(1024)
                    var len: Int
                    while (gzipIS.read(buffer).also { len = it } != -1) {
                        bos.write(buffer, 0, len)
                    }
                    result = bos.toByteArray()
                } catch (var55: Throwable) {
                    var7 = var55
                    throw var55
                } finally {
                    if (var7 != null) {
                        try {
                            gzipIS.close()
                        } catch (var54: Throwable) {
                            var7.addSuppressed(var54)
                        }
                    } else {
                        gzipIS.close()
                    }
                }
            } catch (var57: Throwable) {
                var5 = var57
                throw var57
            } finally {
                if (var5 != null) {
                    try {
                        bos.close()
                    } catch (var53: Throwable) {
                        var5.addSuppressed(var53)
                    }
                } else {
                    bos.close()
                }
            }
        } catch (var59: Throwable) {
            var3 = var59
            throw var59
        } finally {
            if (var3 != null) {
                try {
                    bis.close()
                } catch (var52: Throwable) {
                    var3.addSuppressed(var52)
                }
            } else {
                bis.close()
            }
        }
    } catch (var61: IOException) {
        var61.printStackTrace()
    }
    return result
}
