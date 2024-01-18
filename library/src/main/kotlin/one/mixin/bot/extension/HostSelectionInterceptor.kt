package one.mixin.bot.extension

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import one.mixin.bot.Constants.API.URL
import java.io.IOException

class HostSelectionInterceptor private constructor() : Interceptor {
    @Volatile
    private var host: HttpUrl? = URL.toHttpUrlOrNull()

    private fun setHost(url: String) {
        CURRENT_URL = url
        this.host = url.toHttpUrlOrNull()
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var request = chain.request()
        if (request.header("Upgrade") == "websocket") {
            return chain.proceed(request)
        }
        this.host?.let {
            val newUrl =
                request.url.newBuilder()
                    .host(it.toUrl().toURI().host)
                    .build()
            request =
                request.newBuilder()
                    .url(newUrl)
                    .build()
        }
        return chain.proceed(request)
    }

    companion object {
        var CURRENT_URL: String = URL
            private set

        @Synchronized
        fun get(url: String? = null): HostSelectionInterceptor {
            if (instance == null) {
                if (url != null) {
                    CURRENT_URL = url
                }
                instance = HostSelectionInterceptor()
            }
            return instance as HostSelectionInterceptor
        }

        private var instance: HostSelectionInterceptor? = null
    }
}
