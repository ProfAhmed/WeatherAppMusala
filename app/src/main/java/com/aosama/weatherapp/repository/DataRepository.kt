package com.aosama.weatherapp.repository

import com.aosama.weatherapp.api.ApiService
import com.aosama.weatherapp.models.ApiErrorModel
import com.aosama.weatherapp.utils.Failed
import com.aosama.weatherapp.utils.Loading
import com.aosama.weatherapp.utils.Success
import com.google.gson.Gson
import com.google.gson.JsonParseException
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.util.HashMap

// data repository is responsible to fetch data from remote or local cash

class DataRepository private constructor (private val apiService: ApiService) {

    companion object {
        @Volatile
        private var instance: DataRepository? = null

        @Synchronized
        fun getInstance(apiService: ApiService): DataRepository = instance ?: DataRepository(apiService).also { instance = it }

    }

    fun getCurrentWeatherFlow(data: HashMap<String, String>) = flow {
        emit(Loading)
        try {
            emit(Success(data = apiService.getCurrentWeather(data)))
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    if (throwable.code() == 404) {
                        try {
                            val apiErrorModel: ApiErrorModel =
                                Gson().fromJson(
                                    throwable.response()?.errorBody()?.string(),
                                    ApiErrorModel::class.java
                                )
                            emit(
                                Failed(
                                    message = apiErrorModel.message ?: "Error occured"
                                )
                            )
                        } catch (ex: JsonParseException) {
                            emit(
                                Failed(
                                    message = throwable.message ?: "Error occured"
                                )
                            )
                        }
                    } else {
                        emit(
                            Failed(
                                message = throwable.message ?: "Error occured"
                            )
                        )
                    }
                }
                is IOException -> {
                    emit(
                        Failed(
                            message = throwable.message ?: "Error occured"
                        )
                    )
                }
                else -> {
                    emit(
                        Failed(
                            message = throwable.message ?: "Error occured"
                        )
                    )
                }
            }
        }
    }

}