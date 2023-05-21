package com.example.weatherapp.data.api

import com.example.weatherapp.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("current.json")
    suspend fun getCurrent(
        @Query("key") key: String = BuildConfig.WEATHER_API_KEY,
        @Query("q") query: String
    ): WeatherDTO
}