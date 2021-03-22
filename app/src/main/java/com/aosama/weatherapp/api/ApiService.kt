package com.aosama.weatherapp.api

import com.aosama.weatherapp.models.WeatherResponseModel
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ApiService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(@QueryMap param: HashMap<String, String>): WeatherResponseModel
}