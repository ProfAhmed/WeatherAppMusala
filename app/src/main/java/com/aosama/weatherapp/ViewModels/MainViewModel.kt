package com.aosama.weatherapp.ViewModels

import androidx.lifecycle.ViewModel
import com.aosama.weatherapp.api.ApiService
import com.aosama.weatherapp.repository.DataRepository
import java.util.*

class MainViewModel(private val apiService: ApiService) : ViewModel() {
    val repo = DataRepository.getInstance(apiService)
    fun getCurrentWeatherFlow(data: HashMap<String, String>) = repo.getCurrentWeatherFlow(data)
}


