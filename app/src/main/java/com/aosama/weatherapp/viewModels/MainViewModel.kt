package com.aosama.weatherapp.viewModels

import androidx.lifecycle.ViewModel
import com.aosama.weatherapp.api.ApiService
import com.aosama.weatherapp.repository.DataRepository
import java.util.*

class MainViewModel(apiService: ApiService) : ViewModel() {
    private val repo = DataRepository.getInstance(apiService)
    fun getCurrentWeatherFlow(data: HashMap<String, String>) = repo.getCurrentWeatherFlow(data)
}


