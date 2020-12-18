package one.mixin.bot.api

import retrofit2.Response

class MixinResponse<T>() {

    constructor(response: Response<T>) : this() {
        if (response.isSuccessful) {
            data = response.body()
        } else {
            error = ResponseError(response.code(), response.code(), response.errorBody().toString())
        }
    }

    constructor(response: Throwable) : this() {
        error = ResponseError(500, 500, response.message ?: "")
    }

    var data: T? = null
    var error: ResponseError? = null
    var prev: String? = null
    var next: String? = null

    fun isSuccess(): Boolean {
        return error == null
    }
}
