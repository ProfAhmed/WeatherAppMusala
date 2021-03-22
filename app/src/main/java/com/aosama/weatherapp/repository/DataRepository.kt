package com.aosama.weatherapp.repository

import com.aosama.weatherapp.api.ApiService
import com.aosama.weatherapp.models.ApiErrorModel
import com.aosama.weatherapp.utils.Failed
import com.aosama.weatherapp.utils.Loading
import com.aosama.weatherapp.utils.Success
import com.google.gson.Gson
import kotlinx.coroutines.flow.flow
import org.json.JSONTokener
import retrofit2.HttpException
import java.util.HashMap

// data repository is responsible to fetch data from remote or local cash

//Note it is not recommend using hard code instead of this, Use string values
class DataRepository private constructor(private val apiService: ApiService) {

    companion object {
        @Volatile
        private var instance: DataRepository? = null

        @Synchronized
        fun getInstance(apiService: ApiService): DataRepository =
            instance ?: DataRepository(apiService).also { instance = it }
    }

    fun getCurrentWeatherFlow(data: HashMap<String, String>) = flow {
        emit(Loading)
        try {
            emit(Success(data = apiService.getCurrentWeather(data)))
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    emit(
                        Failed(
                            message = parseErrorBody(throwable)?.message ?: "Unexpected error"
                        )
                    )
                }
                else -> {
                    emit(
                        Failed(
                            message = "Connection error"
                        )
                    )
                }
            }
        }
    }

    private fun parseErrorBody(throwable: HttpException): ApiErrorModel? {
        try {
            val json = JSONTokener(throwable.response()?.errorBody()?.string()).nextValue()
            val errorResponse = Gson().fromJson(json.toString(), ApiErrorModel::class.java)
            errorResponse?.let { return it }
            return null

        } catch (exception: Exception) {
            return null
        }
    }
}