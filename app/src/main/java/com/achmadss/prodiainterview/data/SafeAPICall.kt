package com.achmadss.prodiainterview.data

import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

sealed class APICallResult<out T> {
    data class Success<out Result>(val data: Result) : APICallResult<Result>()
    data class Error(val error: Throwable) : APICallResult<Nothing>()
}

suspend fun <T> safeAPICall(call: suspend () -> Response<T>): APICallResult<T> {
    return withContext(Dispatchers.IO) {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body() ?: throw IllegalStateException("Body is null")
                APICallResult.Success(body)
            } else {
                val errorBody = response.errorBody()?.string()
                val error = Gson().fromJson(errorBody, ErrorBody::class.java)
                APICallResult.Error(IOException(error.message))
            }
        } catch (e: Exception) {
            APICallResult.Error(e)
        }
    }
}

@Keep
data class ErrorBody(
    @SerializedName("message") val message: String
)