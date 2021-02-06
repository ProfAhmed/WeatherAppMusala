package com.aosama.weatherapp.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.aosama.weatherapp.api.ApiService
import com.aosama.weatherapp.models.ApiErrorModel
import com.aosama.weatherapp.utils.Resource
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import java.lang.Exception
import java.util.*

class MainViewModel(private val apiService: ApiService) : ViewModel() {
    fun getCurrentWeather(data: HashMap<String, String>) = liveData(Dispatchers.IO) {
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
                emit(Resource.error(data = null, message = exception.message ?: "Error occured"))
            }
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = "no internet connection"))
        }
    }
}


