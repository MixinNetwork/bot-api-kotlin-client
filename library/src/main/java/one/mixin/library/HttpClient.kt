package one.mixin.library

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import one.mixin.library.api.exception.ClientErrorException
import one.mixin.library.api.exception.ServerErrorException
import one.mixin.library.Constants.API.URL
import one.mixin.library.api.AssetService
import one.mixin.library.api.UserService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.Key
import java.util.Locale
import java.util.concurrent.TimeUnit

class HttpClient(userId: String, sessionId: String, privateKey: Key, debug: Boolean = false) {
    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
        if (debug) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            builder.addNetworkInterceptor(logging)
        }
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.writeTimeout(10, TimeUnit.SECONDS)
        builder.readTimeout(10, TimeUnit.SECONDS)
        builder.pingInterval(15, TimeUnit.SECONDS)
        builder.retryOnConnectionFailure(false)

        builder.addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("User-Agent", Constants.UA)
                .addHeader("Accept-Language", Locale.getDefault().language)
                .addHeader(
                    "Authorization", "Bearer " +
                        signToken(userId, sessionId, chain.request(), privateKey)
                ).build()

            val response = try {
                chain.proceed(request)
            } catch (e: Exception) {
                if (e.message?.contains("502") == true) {
                    throw ServerErrorException(502)
                } else throw e
            }

            if (!response.isSuccessful) {
                val code = response.code
                if (code in 500..599) {
                    throw ServerErrorException(code)
                } else if (code in 400..499) {
                    throw ClientErrorException(code)
                }
            }
            response
        })
        builder.build()
    }

    private val retrofit: Retrofit by lazy {
        val builder = Retrofit.Builder()
            .baseUrl(URL)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
        builder.build()
    }

    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val assetService: AssetService by lazy {
        retrofit.create(AssetService::class.java)
    }
}