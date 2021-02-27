package com.aosama.weatherapp.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.aosama.weatherapp.api.ApiService
import com.aosama.weatherapp.models.ApiErrorModel
import com.aosama.weatherapp.utils.Failer
import com.aosama.weatherapp.utils.Loading
import com.aosama.weatherapp.utils.Resource
import com.aosama.weatherapp.utils.Success
import com.google.gson.Gson
import com.google.gson.JsonParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.lang.Exception
import java.util.*

class MainViewModel(private val apiService: ApiService) : ViewModel() {
    fun getCurrentWeather(data: HashMap<String, String>) = liveData(Dispatchers.IO) {
        emit(Loading)
        try {
            emit(Success(data = apiService.getCurrentWeather(data)))
        } catch (exception: HttpException) {
            if (exception.code() == 404) {
                try {
                    val apiErrorModel: ApiErrorModel =
                        Gson().fromJson(
                            exception.response()?.errorBody()?.string(),
                            ApiErrorModel::class.java
                        )
                    emit(
                        Failer(
                            message = apiErrorModel.message ?: "Error occured"
                        )
                    )
                } catch (ex: JsonParseException) {
                    emit(
                        Failer(
                            message = exception.message ?: "Error occured"
                        )
                    )
                }
            } else {
                emit(Failer(message = exception.message ?: "Error occured"))
            }
        } catch (e: Exception) {
            emit(Failer(message = "no internet connection"))
        }
    }

    fun getCurrentWeatherFlow(data: HashMap<String, String>) =
        flow {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = apiService.getCurrentWeather(data)))
            } catch (exception: HttpException) {
                if (exception.code() == 404) {
                    try {
                        val apiErrorModel: ApiErrorModel =
                            Gson().fromJson(
                                exception.response()?.errorBody()?.string(),
                                ApiErrorModel::class.java
                            )
                        emit(
                            Resource.error(
                                data = null,
                                message = apiErrorModel.message ?: "Error occured"
                            )
                        )
                    } catch (ex: JsonParseException) {
                        emit(
                            Resource.error(
                                data = null,
                                message = exception.message ?: "Error occured"
                            )
                        )
                    }
                } else {
                    emit(
                        Resource.error(
                            data = null,
                            message = exception.message ?: "Error occured"
                        )
                    )
                }
            } catch (e: Exception) {
                emit(Resource.error(data = null, message = "no internet connection"))
            }
        }

    fun foo(): Flow<Int> = flow {
        for (i in 1..1000) {
            delay(1)
            emit(i)
        }
    }

    fun foo2(): LiveData<Int> = liveData(Dispatchers.IO) {
        for (i in 1..1000) {
            delay(1)
            emit(i)
        }
    }
}


