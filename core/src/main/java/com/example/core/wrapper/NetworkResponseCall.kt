package com.example.core.wrapper

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class NetworkResponseCall<T> constructor(
    private val callDelegate: Call<T>,
) : Call<NetworkResultWrapper<T?>> {

    override fun enqueue(callback: Callback<NetworkResultWrapper<T?>>) =
        callDelegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                when (response.code()) {
                    in 200..208 -> {
                        callback.onResponse(
                            this@NetworkResponseCall,
                            Response.success(
                                NetworkResultWrapper.Success(
                                    code = response.code(),
                                    data = response.body(),
                                    header = response.headers().toMultimap()
                                ),
                            ),
                        )
                    }

                    else -> {
                        val jsonObject = parseJson(response)
                        callback.onResponse(
                            this@NetworkResponseCall,
                            Response.success(
                                NetworkResultWrapper.Error(
                                    code = response.code(),
                                    data = jsonObject,
                                    header = response.headers().toMultimap()
                                ),
                            ),
                        )
                    }
                }
            }

            override fun onFailure(call: Call<T>, throwable: Throwable) {
                callback.onResponse(
                    this@NetworkResponseCall,
                    Response.success(NetworkResultWrapper.Exception(throwable)),
                )
                call.cancel()
            }
        })

    override fun clone(): Call<NetworkResultWrapper<T?>> = NetworkResponseCall(callDelegate.clone())

    override fun execute(): Response<NetworkResultWrapper<T?>> =
        throw UnsupportedOperationException("ResponseCall does not support execute.")

    override fun isExecuted(): Boolean = callDelegate.isExecuted

    override fun cancel() = callDelegate.cancel()

    override fun isCanceled(): Boolean = callDelegate.isCanceled

    override fun request(): Request = callDelegate.request()

    override fun timeout(): Timeout = callDelegate.timeout()

    // parseJson function has to be separated from getting json element
    internal fun parseJson(response : Response<T>) : JsonObject {
        return try {
            JsonParser.parseString(
                response.errorBody()?.string()
            ).asJsonObject
        } catch (e: Exception) {
            JsonObject()
        }
    }
}
