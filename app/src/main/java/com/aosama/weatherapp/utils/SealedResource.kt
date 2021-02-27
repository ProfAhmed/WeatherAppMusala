package com.aosama.weatherapp.utils

sealed class SealedResource

data class Success<out T>(val data: T?) : SealedResource()
data class Failer(val message: String?) : SealedResource()
object Loading : SealedResource()

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {
        fun <T> success(data: T): Resource<T> =
            Resource(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String?): Resource<T> =
            Resource(status = Status.ERROR, data = data, message = message)

        fun <T> loading(data: T?): Resource<T> =
            Resource(status = Status.LOADING, data = data, message = null)
    }

}