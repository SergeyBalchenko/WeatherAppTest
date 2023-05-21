package com.example.weatherapp.data.contract

import com.example.weatherapp.data.model.Location
import com.example.weatherapp.data.model.WeatherModel
import retrofit2.HttpException

interface WeatherRepository {

    /**
     * Get current weather by user location
     * @param location user location
     * @throws IOException if there is an IO error during request.
     * @throws HttpException if the request was success but server return an exception.
     */
    suspend fun getWeather(location: Location): WeatherModel
}