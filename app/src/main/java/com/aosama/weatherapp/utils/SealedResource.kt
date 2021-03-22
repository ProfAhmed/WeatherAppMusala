package com.aosama.weatherapp.utils

// Handle network statuses
sealed class SealedResource

data class Success<out T>(val data: T?) : SealedResource()
data class Failed(val message: String?) : SealedResource()
object Loading : SealedResource()
